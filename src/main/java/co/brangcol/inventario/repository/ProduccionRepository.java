package co.brangcol.inventario.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Produccion;

@Repository
public class ProduccionRepository {

	private static final String SELECT = """
			select o.codigo, o.numero, i.codigo as item, i.nombre, u.simbolo, b.nombre as bodega, o.cantidad,
			       o.costo_unitario, o.estado, o.ejecutada_en, o.observacion, count(*) over () as total
			from inventario.orden_ensamble o
			join producto.item i on i.empresa_id = o.empresa_id and i.id = o.item_id
			join parametro.unidad_medida u on u.id = i.unidad_base_id
			join inventario.bodega b on b.empresa_id = o.empresa_id and b.id = o.bodega_id
			""";

	private final JdbcTemplate jdbc;

	public ProduccionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String registrar(String item, String bodega, BigDecimal cantidad, String observacion) {
		return jdbc.queryForObject("select inventario.registrar_produccion(?, ?, ?, ?)", String.class, item, bodega,
				cantidad, observacion);
	}

	public void anular(String codigo, String motivo) {
		jdbc.queryForObject("""
				select 1 from inventario.anular_ensamble((select id from inventario.orden_ensamble where codigo = ?), ?)
				""", Integer.class, codigo, motivo);
	}

	public Pagina<Produccion> listar(int pagina, int tamano) {
		return jdbc.query(SELECT + " where o.estado <> 'BORRADOR' order by o.id desc limit ? offset ?",
				new PaginaExtractor(), tamano, pagina * tamano);
	}

	public Produccion obtener(String codigo) {
		Pagina<Produccion> resultado = jdbc.query(SELECT + " where o.codigo = ?", new PaginaExtractor(), codigo);
		return resultado.items().isEmpty() ? null : resultado.items().get(0);
	}

	private static final class PaginaExtractor implements ResultSetExtractor<Pagina<Produccion>> {

		@Override
		public Pagina<Produccion> extractData(ResultSet rs) throws SQLException {
			List<Produccion> items = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				items.add(new Produccion(rs.getString("codigo"), rs.getString("numero"), rs.getString("item"),
						rs.getString("nombre"), rs.getString("simbolo"), rs.getString("bodega"),
						rs.getBigDecimal("cantidad"), rs.getBigDecimal("costo_unitario"), rs.getString("estado"),
						rs.getObject("ejecutada_en", OffsetDateTime.class), rs.getString("observacion")));
			}
			return new Pagina<>(items, total);
		}

	}

}
