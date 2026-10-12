package co.brangcol.producto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.UnidadAlterna;

@Repository
public class ItemUnidadRepository {

	private static final String ITEM = "(select id from producto.item where codigo = ?)";

	private final JdbcTemplate jdbc;

	public ItemUnidadRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<UnidadAlterna> listar(String itemCodigo) {
		return jdbc.query("""
				select u.codigo_dian, u.nombre, iu.factor, iu.precio_venta, iu.para_compra, iu.para_venta
				from producto.item_unidad iu
				join parametro.unidad_medida u on u.id = iu.unidad_id
				where iu.item_id = %s
				order by iu.factor
				""".formatted(ITEM), new UnidadAlternaMapper(), itemCodigo);
	}

	public void reemplazar(String itemCodigo, List<UnidadAlterna> unidades) {
		jdbc.update("delete from producto.item_unidad where item_id = " + ITEM, itemCodigo);
		for (UnidadAlterna u : unidades) {
			jdbc.update("""
					insert into producto.item_unidad (item_id, unidad_id, factor, precio_venta, para_compra, para_venta)
					values (%s, (select id from parametro.unidad_medida where codigo_dian = ?), ?, ?, ?, ?)
					""".formatted(ITEM), itemCodigo, u.unidad(), u.factor(), u.precioVenta(), u.paraCompra(),
					u.paraVenta());
		}
	}

	private static final class UnidadAlternaMapper implements RowMapper<UnidadAlterna> {

		@Override
		public UnidadAlterna mapRow(ResultSet rs, int fila) throws SQLException {
			return new UnidadAlterna(rs.getString("codigo_dian"), rs.getString("nombre"), rs.getBigDecimal("factor"),
					rs.getBigDecimal("precio_venta"), rs.getBoolean("para_compra"), rs.getBoolean("para_venta"));
		}

	}

}
