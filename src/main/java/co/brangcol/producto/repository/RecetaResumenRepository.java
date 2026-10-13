package co.brangcol.producto.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.RecetaResumen;

@Repository
public class RecetaResumenRepository {

	private final JdbcTemplate jdbc;

	public RecetaResumenRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	// Servicios, combos y fabricados activos con lo que lleva su receta vigente y el costo estimado
	public List<RecetaResumen> listar(String texto) {
		List<Object> parametros = new ArrayList<>();
		StringBuilder sql = new StringBuilder("""
				select i.codigo, i.sku, i.nombre, i.tipo,
				       string_agg(c.nombre || ' ' || trim_scale(rc.cantidad) || ' ' || u.simbolo, ', ' order by c.nombre) as componentes,
				       count(rc.componente_item_id) as cantidad_componentes,
				       round(coalesce(sum(rc.cantidad * (1 + rc.merma_porcentaje / 100) * c.costo_promedio), 0), 2) as costo,
				       round(i.precio_venta * (1 + coalesce(imp.tarifa, 0) / 100), 2) as precio_con_impuesto
				from producto.item i
				left join impuesto.impuesto imp on imp.empresa_id = i.empresa_id and imp.id = i.impuesto_id
				left join producto.receta r on r.empresa_id = i.empresa_id and r.item_id = i.id
				left join producto.receta_version rv on rv.empresa_id = r.empresa_id and rv.receta_id = r.id and rv.es_vigente
				left join producto.receta_componente rc on rc.empresa_id = rv.empresa_id and rc.receta_version_id = rv.id
				left join producto.item c on c.empresa_id = rc.empresa_id and c.id = rc.componente_item_id
				left join parametro.unidad_medida u on u.id = c.unidad_base_id
				where i.activo and i.tipo in ('SERVICIO', 'COMPUESTO', 'FABRICADO')
				""");
		if (texto != null) {
			sql.append(" and unaccent(i.nombre) ilike '%' || unaccent(?) || '%'");
			parametros.add(texto);
		}
		sql.append(" group by i.id, imp.tarifa order by i.nombre");
		return jdbc.query(sql.toString(), (rs, n) -> new RecetaResumen(rs.getString("codigo"), rs.getString("sku"),
				rs.getString("nombre"), rs.getString("tipo"), rs.getString("componentes"),
				rs.getInt("cantidad_componentes"), rs.getBigDecimal("costo"),
				rs.getBigDecimal("precio_con_impuesto")), parametros.toArray());
	}

}
