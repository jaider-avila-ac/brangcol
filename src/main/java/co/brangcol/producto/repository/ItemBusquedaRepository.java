package co.brangcol.producto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.FiltroItems;
import co.brangcol.producto.model.ItemResumen;
import co.brangcol.producto.model.PaginaItems;

@Repository
public class ItemBusquedaRepository {

	private static final String COINCIDE = """
			 and (unaccent(i.nombre) ilike '%' || unaccent(?) || '%'
			      or i.sku ilike ? || '%'
			      or exists (select 1 from producto.item_codigo_barras b where b.empresa_id = i.empresa_id and b.item_id = i.id and b.codigo_barras = ?)
			      or word_similarity(unaccent(?), unaccent(i.nombre)) > 0.3)
			""";

	private final JdbcTemplate jdbc;

	public ItemBusquedaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public PaginaItems buscar(FiltroItems filtro) {
		List<Object> parametros = new ArrayList<>();
		StringBuilder sql = new StringBuilder("""
				select i.codigo, i.sku, i.nombre, i.tipo, c.nombre as categoria, u.simbolo, i.precio_venta,
				       round(i.precio_venta * (1 + coalesce(imp.tarifa, 0) / 100), 2) as precio_con_impuesto,
				       i.costo_promedio, i.activo,
				       case when i.maneja_inventario then coalesce(ex.cantidad, 0) end as existencia, ex.minimo,
				       rec.componentes, coalesce(rec.cantidad, 0) as cantidad_componentes, count(*) over () as total
				from producto.item i
				join parametro.unidad_medida u on u.id = i.unidad_base_id
				left join producto.categoria c on c.empresa_id = i.empresa_id and c.id = i.categoria_id
				left join impuesto.impuesto imp on imp.empresa_id = i.empresa_id and imp.id = i.impuesto_id
				left join lateral (
				    select sum(e.cantidad) as cantidad, sum(e.minimo) as minimo
				    from inventario.existencia e where e.empresa_id = i.empresa_id and e.item_id = i.id
				) ex on i.maneja_inventario
				left join lateral (
				    select string_agg(k.nombre || ' ' || trim_scale(round(rc.cantidad / coalesce(iu.factor, 1), 4)) || ' '
				                      || coalesce(pu.simbolo, ku.simbolo), ', ' order by k.nombre) as componentes,
				           count(*) as cantidad
				    from producto.receta r
				    join producto.receta_version rv on rv.empresa_id = r.empresa_id and rv.receta_id = r.id and rv.es_vigente
				    join producto.receta_componente rc on rc.empresa_id = rv.empresa_id and rc.receta_version_id = rv.id
				    join producto.item k on k.empresa_id = rc.empresa_id and k.id = rc.componente_item_id
				    join parametro.unidad_medida ku on ku.id = k.unidad_base_id
				    left join parametro.unidad_medida pu on pu.id = rc.presentacion_id
				    left join producto.item_unidad iu on iu.empresa_id = k.empresa_id and iu.item_id = k.id and iu.unidad_id = rc.presentacion_id
				    where r.empresa_id = i.empresa_id and r.item_id = i.id
				) rec on i.tipo in ('SERVICIO', 'COMPUESTO', 'FABRICADO')
				where true
				""");
		if (!filtro.incluirInactivos()) {
			sql.append(" and i.activo");
		}
		if (filtro.soloBajoMinimo()) {
			sql.append(" and ex.minimo is not null and coalesce(ex.cantidad, 0) <= ex.minimo");
		}
		if (filtro.tipo() != null) {
			sql.append(" and i.tipo = ?");
			parametros.add(filtro.tipo());
		}
		if (filtro.categoria() != null) {
			sql.append(" and c.codigo = ?");
			parametros.add(filtro.categoria());
		}
		if (filtro.soloComponentes()) {
			sql.append(" and (i.maneja_inventario or i.tipo = 'COMPUESTO')");
		}
		if (filtro.texto() != null) {
			sql.append(COINCIDE);
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			parametros.add(filtro.texto());
			sql.append(" order by word_similarity(unaccent(?), unaccent(i.nombre)) desc, i.nombre");
			parametros.add(filtro.texto());
		}
		else {
			sql.append(" order by i.nombre");
		}
		sql.append(" limit ? offset ?");
		parametros.add(filtro.tamano());
		parametros.add(filtro.pagina() * filtro.tamano());
		return jdbc.query(sql.toString(), new PaginaExtractor(), parametros.toArray());
	}

	private static final class PaginaExtractor implements ResultSetExtractor<PaginaItems> {

		@Override
		public PaginaItems extractData(ResultSet rs) throws SQLException {
			List<ItemResumen> items = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				items.add(new ItemResumen(rs.getString("codigo"), rs.getString("sku"), rs.getString("nombre"),
						rs.getString("tipo"), rs.getString("categoria"), rs.getString("simbolo"),
						rs.getBigDecimal("precio_venta"), rs.getBigDecimal("precio_con_impuesto"),
						rs.getBigDecimal("costo_promedio"), rs.getBoolean("activo"), rs.getBigDecimal("existencia"),
						rs.getBigDecimal("minimo"), rs.getString("componentes"), rs.getInt("cantidad_componentes")));
			}
			return new PaginaItems(items, total);
		}

	}

}
