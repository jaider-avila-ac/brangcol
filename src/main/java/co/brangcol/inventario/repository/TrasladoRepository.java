package co.brangcol.inventario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.inventario.model.LineaInventario;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Traslado;
import co.brangcol.inventario.model.TrasladoResumen;

@Repository
public class TrasladoRepository {

	private static final String ENCABEZADO = """
			select t.codigo, t.numero, o.nombre as origen, d.nombre as destino, t.estado, t.enviado_en, t.recibido_en,
			       t.observacion
			from inventario.traslado t
			join inventario.bodega o on o.empresa_id = t.empresa_id and o.id = t.bodega_origen_id
			join inventario.bodega d on d.empresa_id = t.empresa_id and d.id = t.bodega_destino_id
			""";

	private final JdbcTemplate jdbc;

	public TrasladoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String registrar(String origen, String destino, String observacion, String lineasJson) {
		return jdbc.queryForObject("select inventario.registrar_traslado(?, ?, ?, ?::jsonb)", String.class, origen,
				destino, observacion, lineasJson);
	}

	public void recibir(String codigo, String recibidosJson) {
		jdbc.queryForObject("select 1 from inventario.recibir_traslado_por_codigo(?, ?::jsonb)", Integer.class, codigo,
				recibidosJson);
	}

	public void anular(String codigo, String motivo) {
		jdbc.queryForObject("""
				select 1 from inventario.anular_traslado((select id from inventario.traslado where codigo = ?), ?)
				""", Integer.class, codigo, motivo);
	}

	public Pagina<TrasladoResumen> listar(int pagina, int tamano) {
		return jdbc.query("""
				select t.codigo, t.numero, o.nombre as origen, d.nombre as destino, t.estado, t.enviado_en,
				       (select count(*) from inventario.traslado_linea l where l.empresa_id = t.empresa_id and l.traslado_id = t.id) as lineas,
				       count(*) over () as total
				from inventario.traslado t
				join inventario.bodega o on o.empresa_id = t.empresa_id and o.id = t.bodega_origen_id
				join inventario.bodega d on d.empresa_id = t.empresa_id and d.id = t.bodega_destino_id
				where t.estado <> 'BORRADOR'
				order by t.id desc limit ? offset ?
				""", new PaginaExtractor(), tamano, pagina * tamano);
	}

	public Traslado obtener(String codigo) {
		List<Traslado> encontrados = jdbc.query(ENCABEZADO + " where t.codigo = ?",
				(rs, n) -> new Traslado(rs.getString("codigo"), rs.getString("numero"), rs.getString("origen"),
						rs.getString("destino"), rs.getString("estado"),
						rs.getObject("enviado_en", OffsetDateTime.class),
						rs.getObject("recibido_en", OffsetDateTime.class), rs.getString("observacion"),
						lineas(codigo)),
				codigo);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	private List<LineaInventario> lineas(String codigo) {
		return jdbc.query("""
				select i.codigo, i.nombre, u.simbolo, l.cantidad_enviada, l.cantidad_recibida
				from inventario.traslado t
				join inventario.traslado_linea l on l.empresa_id = t.empresa_id and l.traslado_id = t.id
				join producto.item i on i.empresa_id = l.empresa_id and i.id = l.item_id
				join parametro.unidad_medida u on u.id = i.unidad_base_id
				where t.codigo = ?
				order by l.id
				""", (rs, n) -> new LineaInventario(rs.getString("codigo"), rs.getString("nombre"),
				rs.getString("simbolo"), null, rs.getBigDecimal("cantidad_enviada"), null,
				rs.getBigDecimal("cantidad_recibida")), codigo);
	}

	private static final class PaginaExtractor implements ResultSetExtractor<Pagina<TrasladoResumen>> {

		@Override
		public Pagina<TrasladoResumen> extractData(ResultSet rs) throws SQLException {
			List<TrasladoResumen> items = new ArrayList<>();
			long total = 0;
			while (rs.next()) {
				total = rs.getLong("total");
				items.add(new TrasladoResumen(rs.getString("codigo"), rs.getString("numero"), rs.getString("origen"),
						rs.getString("destino"), rs.getString("estado"),
						rs.getObject("enviado_en", OffsetDateTime.class), rs.getInt("lineas")));
			}
			return new Pagina<>(items, total);
		}

	}

}
