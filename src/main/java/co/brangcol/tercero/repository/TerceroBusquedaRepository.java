package co.brangcol.tercero.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.tercero.model.FiltroTerceros;
import co.brangcol.tercero.model.PaginaTerceros;
import co.brangcol.tercero.model.RolTercero;
import co.brangcol.tercero.model.TerceroResumen;

@Repository
public class TerceroBusquedaRepository {

	// La busqueda tolera errores de escritura (similitud por trigramas) y no distingue tildes
	private static final String COINCIDE = """
			 and (unaccent(t.nombre_completo) ilike '%' || unaccent(?) || '%'
			      or t.numero_documento like ? || '%'
			      or t.telefono like '%' || ? || '%'
			      or word_similarity(unaccent(?), unaccent(t.nombre_completo)) > 0.3)
			""";

	private final JdbcTemplate jdbc;

	public TerceroBusquedaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public PaginaTerceros buscar(FiltroTerceros filtro) {
		List<Object> parametros = new ArrayList<>();
		StringBuilder sql = new StringBuilder("""
				select t.codigo, t.nombre_completo, td.abreviatura, t.numero_documento, t.dv, t.telefono, t.correo,
				       m.nombre as municipio, c.tercero_id is not null as es_cliente, p.tercero_id is not null as es_proveedor,
				       coalesce(sc.total_debe, 0) as debe, coalesce(sc.total_vencido, 0) as vencido,
				       coalesce(sc.saldo_a_favor, 0) as saldo_favor, coalesce(cp.total, 0) as le_debemos,
				       t.activo, t.estado_datos, count(*) over () as total
				from tercero.tercero t
				left join parametro.tipo_documento_identidad td on td.id = t.tipo_documento_id
				left join parametro.municipio m on m.id = t.municipio_id
				left join tercero.cliente c on c.empresa_id = t.empresa_id and c.tercero_id = t.id
				left join tercero.proveedor p on p.empresa_id = t.empresa_id and p.tercero_id = t.id
				left join reportes.v_saldo_cliente sc on sc.empresa_id = t.empresa_id and sc.tercero_id = t.id
				left join reportes.v_cuentas_por_pagar cp on cp.empresa_id = t.empresa_id and cp.tercero_id = t.id
				where not t.es_consumidor_final
				""");
		if (!filtro.incluirInactivos()) {
			sql.append(" and t.activo");
		}
		if (filtro.rol() == RolTercero.CLIENTES) {
			sql.append(" and c.tercero_id is not null");
		}
		if (filtro.rol() == RolTercero.PROVEEDORES) {
			sql.append(" and p.tercero_id is not null");
		}
		if (filtro.soloDeben()) {
			sql.append(" and coalesce(sc.total_debe, 0) > 0");
		}
		boolean conTexto = filtro.texto() != null && !filtro.texto().isBlank();
		if (conTexto) {
			sql.append(COINCIDE);
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			sql.append(" order by word_similarity(unaccent(?), unaccent(t.nombre_completo)) desc, t.nombre_completo");
			parametros.add(filtro.texto());
		}
		else {
			sql.append(" order by t.nombre_completo");
		}
		sql.append(" limit ? offset ?");
		parametros.add(filtro.tamano());
		parametros.add(filtro.pagina() * filtro.tamano());
		return jdbc.query(sql.toString(), new PaginaExtractor(), parametros.toArray());
	}

	private static final class PaginaExtractor implements ResultSetExtractor<PaginaTerceros> {

		@Override
		public PaginaTerceros extractData(ResultSet rs) throws SQLException {
			List<TerceroResumen> terceros = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				terceros.add(new TerceroResumen(rs.getString("codigo"), rs.getString("nombre_completo"),
						rs.getString("abreviatura"), rs.getString("numero_documento"), rs.getString("dv"),
						rs.getString("telefono"), rs.getString("correo"), rs.getString("municipio"),
						rs.getBoolean("es_cliente"), rs.getBoolean("es_proveedor"), rs.getBigDecimal("debe"),
						rs.getBigDecimal("vencido"), rs.getBigDecimal("saldo_favor"), rs.getBigDecimal("le_debemos"),
						rs.getBoolean("activo"), rs.getString("estado_datos")));
			}
			return new PaginaTerceros(terceros, total);
		}

	}

}
