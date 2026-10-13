package co.brangcol.inventario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.inventario.model.FiltroKardex;
import co.brangcol.inventario.model.Movimiento;
import co.brangcol.inventario.model.Pagina;

@Repository
public class KardexRepository {

	private final JdbcTemplate jdbc;

	public KardexRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Pagina<Movimiento> buscar(FiltroKardex filtro) {
		List<Object> parametros = new ArrayList<>();
		StringBuilder sql = new StringBuilder("""
				select m.fecha, m.tipo, t.nombre as tipo_nombre, t.signo, m.cantidad, m.saldo_cantidad, m.costo_unitario,
				       m.costo_promedio, b.nombre as bodega, m.documento_tipo, m.observacion,
				       case m.documento_tipo
				           when 'AJUSTE' then (select a.numero from inventario.ajuste a where a.empresa_id = m.empresa_id and a.id = m.documento_id)
				           when 'TRASLADO' then (select tr.numero from inventario.traslado tr where tr.empresa_id = m.empresa_id and tr.id = m.documento_id)
				           when 'ENSAMBLE' then (select oe.numero from inventario.orden_ensamble oe where oe.empresa_id = m.empresa_id and oe.id = m.documento_id)
				       end as numero,
				       count(*) over () as total
				from inventario.movimiento m
				join inventario.tipo_movimiento t on t.clave = m.tipo
				join inventario.bodega b on b.empresa_id = m.empresa_id and b.id = m.bodega_id
				join producto.item i on i.empresa_id = m.empresa_id and i.id = m.item_id
				where i.codigo = ?
				""");
		parametros.add(filtro.item());
		if (filtro.bodega() != null) {
			sql.append(" and b.codigo = ?");
			parametros.add(filtro.bodega());
		}
		if (filtro.desde() != null) {
			sql.append(" and m.fecha >= ?::date");
			parametros.add(filtro.desde());
		}
		if (filtro.hasta() != null) {
			sql.append(" and m.fecha < ?::date + 1");
			parametros.add(filtro.hasta());
		}
		sql.append(" order by m.fecha desc, m.id desc limit ? offset ?");
		parametros.add(filtro.tamano());
		parametros.add(filtro.pagina() * filtro.tamano());
		return jdbc.query(sql.toString(), new PaginaExtractor(), parametros.toArray());
	}

	private static final class PaginaExtractor implements ResultSetExtractor<Pagina<Movimiento>> {

		@Override
		public Pagina<Movimiento> extractData(ResultSet rs) throws SQLException {
			List<Movimiento> items = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				items.add(new Movimiento(rs.getObject("fecha", OffsetDateTime.class), rs.getString("tipo"),
						rs.getString("tipo_nombre"), rs.getInt("signo"), rs.getBigDecimal("cantidad"),
						rs.getBigDecimal("saldo_cantidad"), rs.getBigDecimal("costo_unitario"),
						rs.getBigDecimal("costo_promedio"), rs.getString("bodega"), rs.getString("documento_tipo"),
						rs.getString("numero"), rs.getString("observacion")));
			}
			return new Pagina<>(items, total);
		}

	}

}
