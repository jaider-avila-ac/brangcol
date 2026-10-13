package co.brangcol.inventario.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.inventario.model.Existencia;
import co.brangcol.inventario.model.FiltroExistencias;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.ResumenInventario;

@Repository
public class ExistenciaRepository {

	private static final String COINCIDE = """
			 and (unaccent(i.nombre) ilike '%' || unaccent(?) || '%'
			      or i.sku ilike ? || '%'
			      or exists (select 1 from producto.item_codigo_barras cb where cb.empresa_id = i.empresa_id and cb.item_id = i.id and cb.codigo_barras = ?)
			      or word_similarity(unaccent(?), unaccent(i.nombre)) > 0.3)
			""";

	private final JdbcTemplate jdbc;

	public ExistenciaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	// Sin bodega se suman todas las bodegas
	public Pagina<Existencia> buscar(FiltroExistencias filtro) {
		List<Object> parametros = new ArrayList<>();
		StringBuilder sql = new StringBuilder("""
				select i.codigo, i.sku, i.nombre, u.simbolo, i.permite_fraccion, i.costo_promedio,
				       coalesce(sum(e.cantidad), 0) as cantidad, sum(e.minimo) as minimo, count(*) over () as total
				from producto.item i
				join parametro.unidad_medida u on u.id = i.unidad_base_id
				left join inventario.existencia e on e.empresa_id = i.empresa_id and e.item_id = i.id
				""");
		if (filtro.bodega() != null) {
			sql.append(" and e.bodega_id = (select b.id from inventario.bodega b where b.codigo = ?)");
			parametros.add(filtro.bodega());
		}
		sql.append(" where i.maneja_inventario and i.activo");
		if (filtro.texto() != null) {
			sql.append(COINCIDE);
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
		}
		sql.append(" group by i.id, u.simbolo");
		if (filtro.soloBajoMinimo()) {
			sql.append(" having sum(e.minimo) is not null and coalesce(sum(e.cantidad), 0) <= sum(e.minimo)");
		}
		sql.append(" order by i.nombre limit ? offset ?");
		parametros.add(filtro.tamano());
		parametros.add(filtro.pagina() * filtro.tamano());
		return jdbc.query(sql.toString(), new PaginaExtractor(), parametros.toArray());
	}

	public ResumenInventario resumen() {
		return jdbc.queryForObject("""
				select count(*) as productos,
				       coalesce(sum(x.cantidad * x.costo_promedio), 0) as valor,
				       count(*) filter (where x.minimo is not null and x.cantidad <= x.minimo) as bajo_minimo
				from (
				    select i.costo_promedio, coalesce(sum(e.cantidad), 0) as cantidad, sum(e.minimo) as minimo
				    from producto.item i
				    left join inventario.existencia e on e.empresa_id = i.empresa_id and e.item_id = i.id
				    where i.maneja_inventario and i.activo
				    group by i.id
				) x
				""", (rs, n) -> new ResumenInventario(rs.getLong("productos"),
				rs.getBigDecimal("valor").setScale(2, java.math.RoundingMode.HALF_UP), rs.getLong("bajo_minimo")));
	}

	public int definirMinimo(String bodega, String item, BigDecimal minimo) {
		return jdbc.update("""
				update inventario.existencia e set minimo = ?
				from inventario.bodega b, producto.item i
				where b.empresa_id = e.empresa_id and b.id = e.bodega_id and b.codigo = ?
				  and i.empresa_id = e.empresa_id and i.id = e.item_id and i.codigo = ?
				""", minimo, bodega, item);
	}

	private static final class PaginaExtractor implements ResultSetExtractor<Pagina<Existencia>> {

		@Override
		public Pagina<Existencia> extractData(ResultSet rs) throws SQLException {
			List<Existencia> items = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				items.add(new Existencia(rs.getString("codigo"), rs.getString("sku"), rs.getString("nombre"),
						rs.getString("simbolo"), rs.getBoolean("permite_fraccion"), rs.getBigDecimal("cantidad"),
						rs.getBigDecimal("minimo"), rs.getBigDecimal("costo_promedio")));
			}
			return new Pagina<>(items, total);
		}

	}

}
