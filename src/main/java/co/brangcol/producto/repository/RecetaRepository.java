package co.brangcol.producto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.ComponenteReceta;
import co.brangcol.producto.model.Receta;

@Repository
public class RecetaRepository {

	private final JdbcTemplate jdbc;

	public RecetaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Receta vigente(String itemCodigo) {
		return jdbc.query("""
				select rv.numero, rv.bloqueada, c.codigo, c.nombre, coalesce(pu.simbolo, u.simbolo) as simbolo, pu.codigo_dian as presentacion,
				       round(rc.cantidad / coalesce(iu.factor, 1), 4) as cantidad, rc.merma_porcentaje,
				       c.costo_promedio * coalesce(iu.factor, 1) as costo_promedio
				from producto.item i
				join producto.receta r on r.empresa_id = i.empresa_id and r.item_id = i.id
				join producto.receta_version rv on rv.empresa_id = r.empresa_id and rv.receta_id = r.id and rv.es_vigente
				left join producto.receta_componente rc on rc.empresa_id = rv.empresa_id and rc.receta_version_id = rv.id
				left join producto.item c on c.empresa_id = rc.empresa_id and c.id = rc.componente_item_id
				left join parametro.unidad_medida u on u.id = c.unidad_base_id
				left join parametro.unidad_medida pu on pu.id = rc.presentacion_id
				left join producto.item_unidad iu on iu.empresa_id = c.empresa_id and iu.item_id = c.id and iu.unidad_id = rc.presentacion_id
				where i.codigo = ?
				order by c.nombre
				""", new RecetaExtractor(), itemCodigo);
	}

	public int guardar(String itemCodigo, String componentesJson) {
		Integer version = jdbc.queryForObject("select producto.guardar_receta(?, ?::jsonb)", Integer.class, itemCodigo,
				componentesJson);
		return version == null ? 0 : version;
	}

	private static final class RecetaExtractor implements ResultSetExtractor<Receta> {

		@Override
		public Receta extractData(ResultSet rs) throws SQLException {
			int version = 0;
			boolean bloqueada = false;
			List<ComponenteReceta> componentes = new ArrayList<>();
			while (rs.next()) {
				version = rs.getInt("numero");
				bloqueada = rs.getBoolean("bloqueada");
				if (rs.getString("codigo") != null) {
					componentes.add(new ComponenteReceta(rs.getString("codigo"), rs.getString("nombre"),
							rs.getString("simbolo"), rs.getBigDecimal("cantidad"), rs.getBigDecimal("merma_porcentaje"),
							rs.getBigDecimal("costo_promedio"), rs.getString("presentacion")));
				}
			}
			if (version == 0) {
				return null;
			}
			return new Receta(version, bloqueada, componentes);
		}

	}

}
