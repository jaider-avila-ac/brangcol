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
				       i.costo_promedio, i.activo, a.codigo as foto, count(*) over () as total
				from producto.item i
				join parametro.unidad_medida u on u.id = i.unidad_base_id
				left join producto.categoria c on c.empresa_id = i.empresa_id and c.id = i.categoria_id
				left join impuesto.impuesto imp on imp.empresa_id = i.empresa_id and imp.id = i.impuesto_id
				left join transversal.archivo a on a.empresa_id = i.empresa_id and a.entidad = 'ITEM_FOTO'
				     and a.entidad_id = i.id and a.eliminado_en is null
				where true
				""");
		if (!filtro.incluirInactivos()) {
			sql.append(" and i.activo");
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
						rs.getBigDecimal("costo_promedio"), rs.getBoolean("activo"), rs.getString("foto")));
			}
			return new PaginaItems(items, total);
		}

	}

}
