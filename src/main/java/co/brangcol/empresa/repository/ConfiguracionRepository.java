package co.brangcol.empresa.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.empresa.model.ConfiguracionEmpresa;

@Repository
public class ConfiguracionRepository {

	private final JdbcTemplate jdbc;

	public ConfiguracionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public ConfiguracionEmpresa obtener() {
		return jdbc.queryForObject("""
				select decimales_cantidad, decimales_precio, controla_inventario, permite_inventario_negativo,
				       max_sesiones_por_usuario, minutos_inactividad, aplicacion_pagos
				from plataforma.empresa_configuracion
				""", new ConfiguracionMapper());
	}

	public void actualizar(ConfiguracionEmpresa configuracion) {
		jdbc.update("""
				update plataforma.empresa_configuracion
				set decimales_cantidad = ?, decimales_precio = ?, controla_inventario = ?, permite_inventario_negativo = ?,
				    max_sesiones_por_usuario = ?, minutos_inactividad = ?, aplicacion_pagos = ?
				""", configuracion.decimalesCantidad(), configuracion.decimalesPrecio(),
				configuracion.controlaInventario(), configuracion.permiteInventarioNegativo(),
				configuracion.maxSesionesPorUsuario(), configuracion.minutosInactividad(),
				configuracion.aplicacionPagos());
	}

	private static final class ConfiguracionMapper implements RowMapper<ConfiguracionEmpresa> {

		@Override
		public ConfiguracionEmpresa mapRow(ResultSet rs, int fila) throws SQLException {
			return new ConfiguracionEmpresa(rs.getInt("decimales_cantidad"), rs.getInt("decimales_precio"),
					rs.getBoolean("controla_inventario"), rs.getBoolean("permite_inventario_negativo"),
					rs.getInt("max_sesiones_por_usuario"), rs.getInt("minutos_inactividad"),
					rs.getString("aplicacion_pagos"));
		}

	}

}
