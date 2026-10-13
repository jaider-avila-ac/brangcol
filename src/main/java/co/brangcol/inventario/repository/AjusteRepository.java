package co.brangcol.inventario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.inventario.model.Ajuste;
import co.brangcol.inventario.model.AjusteResumen;
import co.brangcol.inventario.model.LineaInventario;
import co.brangcol.inventario.model.Pagina;

@Repository
public class AjusteRepository {

	private final JdbcTemplate jdbc;

	public AjusteRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String registrar(String clase, String bodega, String motivo, String lineasJson) {
		return jdbc.queryForObject("select inventario.registrar_ajuste(?, ?, ?, ?::jsonb)", String.class, clase, bodega,
				motivo, lineasJson);
	}

	public void anular(String codigo, String motivo) {
		jdbc.queryForObject("""
				select 1 from inventario.anular_ajuste((select id from inventario.ajuste where codigo = ?), ?)
				""", Integer.class, codigo, motivo);
	}

	public Pagina<AjusteResumen> listar(int pagina, int tamano) {
		return jdbc.query("""
				select a.codigo, a.numero, a.clase, b.nombre as bodega, a.fecha, a.motivo, a.estado,
				       (select count(*) from inventario.ajuste_linea l where l.empresa_id = a.empresa_id and l.ajuste_id = a.id) as lineas,
				       count(*) over () as total
				from inventario.ajuste a
				join inventario.bodega b on b.empresa_id = a.empresa_id and b.id = a.bodega_id
				where a.estado <> 'BORRADOR'
				order by a.id desc limit ? offset ?
				""", new PaginaExtractor(), tamano, pagina * tamano);
	}

	public Ajuste obtener(String codigo) {
		List<Ajuste> encontrados = jdbc.query("""
				select a.codigo, a.numero, a.clase, b.nombre as bodega, a.fecha, a.motivo, a.estado
				from inventario.ajuste a
				join inventario.bodega b on b.empresa_id = a.empresa_id and b.id = a.bodega_id
				where a.codigo = ?
				""", (rs, n) -> new Ajuste(rs.getString("codigo"), rs.getString("numero"), rs.getString("clase"),
				rs.getString("bodega"), rs.getObject("fecha", LocalDate.class), rs.getString("motivo"),
				rs.getString("estado"), lineas(codigo)), codigo);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	private List<LineaInventario> lineas(String codigo) {
		return jdbc.query("""
				select i.codigo, i.nombre, u.simbolo, l.sentido, l.cantidad, l.costo_unitario
				from inventario.ajuste a
				join inventario.ajuste_linea l on l.empresa_id = a.empresa_id and l.ajuste_id = a.id
				join producto.item i on i.empresa_id = l.empresa_id and i.id = l.item_id
				join parametro.unidad_medida u on u.id = i.unidad_base_id
				where a.codigo = ?
				order by l.id
				""", (rs, n) -> new LineaInventario(rs.getString("codigo"), rs.getString("nombre"),
				rs.getString("simbolo"), rs.getString("sentido"), rs.getBigDecimal("cantidad"),
				rs.getBigDecimal("costo_unitario"), null), codigo);
	}

	private static final class PaginaExtractor implements ResultSetExtractor<Pagina<AjusteResumen>> {

		@Override
		public Pagina<AjusteResumen> extractData(ResultSet rs) throws SQLException {
			List<AjusteResumen> items = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				items.add(new AjusteResumen(rs.getString("codigo"), rs.getString("numero"), rs.getString("clase"),
						rs.getString("bodega"), rs.getObject("fecha", LocalDate.class), rs.getString("motivo"),
						rs.getString("estado"), rs.getInt("lineas")));
			}
			return new Pagina<>(items, total);
		}

	}

}
