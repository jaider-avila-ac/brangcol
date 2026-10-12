package co.brangcol.producto.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CodigoBarrasRepository {

	private static final String ITEM = "(select id from producto.item where codigo = ?)";

	private final JdbcTemplate jdbc;

	public CodigoBarrasRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<String> listar(String itemCodigo) {
		return jdbc.queryForList(
				"select codigo_barras from producto.item_codigo_barras where item_id = " + ITEM + " order by codigo_barras",
				String.class, itemCodigo);
	}

	public void reemplazar(String itemCodigo, List<String> codigos) {
		jdbc.update("delete from producto.item_codigo_barras where item_id = " + ITEM, itemCodigo);
		for (String codigo : codigos) {
			jdbc.update("insert into producto.item_codigo_barras (item_id, codigo_barras) values (" + ITEM + ", ?)",
					itemCodigo, codigo);
		}
	}

	// Nombre del otro producto que ya usa ese codigo de barras (null si esta libre)
	public String usadoPorOtro(String codigoBarras, String itemCodigo) {
		List<String> nombres = jdbc.queryForList("""
				select i.nombre from producto.item_codigo_barras b
				join producto.item i on i.empresa_id = b.empresa_id and i.id = b.item_id
				where b.codigo_barras = ? and i.codigo is distinct from ?
				""", String.class, codigoBarras, itemCodigo);
		return nombres.isEmpty() ? null : nombres.get(0);
	}

}
