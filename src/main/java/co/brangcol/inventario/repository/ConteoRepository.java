package co.brangcol.inventario.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConteoRepository {

	private final JdbcTemplate jdbc;

	public ConteoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	// Devuelve el codigo del ajuste con las diferencias o null si todo cuadro
	public String registrar(String bodega, String observacion, String lineasJson) {
		return jdbc.queryForObject("select inventario.registrar_conteo(?, ?, ?::jsonb)", String.class, bodega,
				observacion, lineasJson);
	}

}
