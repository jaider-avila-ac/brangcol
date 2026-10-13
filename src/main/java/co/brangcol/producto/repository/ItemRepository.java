package co.brangcol.producto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.DatosItem;
import co.brangcol.producto.model.Item;

@Repository
public class ItemRepository {

	private static final String REFERENCIAS = """
			(select id from producto.categoria where codigo = ?), (select id from producto.marca where codigo = ?),
			(select id from parametro.unidad_medida where codigo_dian = ?), ?, (select id from impuesto.impuesto where codigo = ?)
			""";

	private final JdbcTemplate jdbc;

	public ItemRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String crear(DatosItem d) {
		return jdbc.queryForObject("""
				insert into producto.item (tipo, sku, nombre, descripcion, categoria_id, marca_id, unidad_base_id,
				    permite_fraccion, impuesto_id, precio_venta, se_vende, se_compra)
				values (?, ?, ?, ?, %s, ?, ?, ?)
				returning codigo
				""".formatted(REFERENCIAS), String.class, d.tipo(), d.sku(), d.nombre(), d.descripcion(), d.categoria(),
				d.marca(), d.unidad(), d.permiteFraccion(), d.impuesto(), d.precioVenta(), d.seVende(), d.seCompra());
	}

	public int actualizar(String codigo, DatosItem d) {
		return jdbc.update("""
				update producto.item
				set (tipo, sku, nombre, descripcion, categoria_id, marca_id, unidad_base_id, permite_fraccion, impuesto_id,
				     precio_venta, se_vende, se_compra) = (?, ?, ?, ?, %s, ?, ?, ?)
				where codigo = ?
				""".formatted(REFERENCIAS), d.tipo(), d.sku(), d.nombre(), d.descripcion(), d.categoria(), d.marca(),
				d.unidad(), d.permiteFraccion(), d.impuesto(), d.precioVenta(), d.seVende(), d.seCompra(), codigo);
	}

	public int cambiarEstado(String codigo, boolean activo) {
		return jdbc.update("update producto.item set activo = ? where codigo = ?", activo, codigo);
	}

	public Item obtener(String codigo) {
		List<Item> items = jdbc.query("""
				select i.codigo, i.sku, i.nombre, i.descripcion, i.tipo, i.se_vende, i.se_compra, i.maneja_inventario,
				       c.codigo as categoria, c.nombre as categoria_nombre, m.codigo as marca, m.nombre as marca_nombre,
				       u.codigo_dian as unidad, u.simbolo as unidad_simbolo, i.permite_fraccion, imp.codigo as impuesto,
				       imp.tarifa, i.precio_venta, i.costo_promedio, i.ultimo_costo, i.activo
				from producto.item i
				join parametro.unidad_medida u on u.id = i.unidad_base_id
				left join producto.categoria c on c.empresa_id = i.empresa_id and c.id = i.categoria_id
				left join producto.marca m on m.empresa_id = i.empresa_id and m.id = i.marca_id
				left join impuesto.impuesto imp on imp.empresa_id = i.empresa_id and imp.id = i.impuesto_id
				where i.codigo = ?
				""", new ItemMapper(), codigo);
		return items.isEmpty() ? null : items.get(0);
	}

	public String nombrePorSku(String sku) {
		List<String> nombres = jdbc.queryForList("select codigo || '|' || nombre from producto.item where sku = ?",
				String.class, sku);
		return nombres.isEmpty() ? null : nombres.get(0);
	}

	public String siguienteSku() {
		return jdbc.queryForObject("""
				select 'P' || lpad((coalesce(max(substring(sku from '^P([0-9]{1,9})$')::bigint), 0) + 1)::text, 5, '0')
				from producto.item
				""", String.class);
	}

	private static final class ItemMapper implements RowMapper<Item> {

		@Override
		public Item mapRow(ResultSet rs, int fila) throws SQLException {
			return new Item(rs.getString("codigo"), rs.getString("sku"), rs.getString("nombre"),
					rs.getString("descripcion"), rs.getString("tipo"), rs.getBoolean("se_vende"),
					rs.getBoolean("se_compra"), rs.getBoolean("maneja_inventario"), rs.getString("categoria"),
					rs.getString("categoria_nombre"), rs.getString("marca"), rs.getString("marca_nombre"),
					rs.getString("unidad"), rs.getString("unidad_simbolo"), rs.getBoolean("permite_fraccion"),
					rs.getString("impuesto"), rs.getBigDecimal("tarifa"), rs.getBigDecimal("precio_venta"),
					rs.getBigDecimal("costo_promedio"), rs.getBigDecimal("ultimo_costo"), rs.getBoolean("activo"),
					new ArrayList<>(), new ArrayList<>());
		}

	}

}
