package co.brangcol.auth.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContrasenaRepository {

	private final JdbcTemplate jdbc;

	public ContrasenaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void cambiarMiContrasena(String actual, String nueva) {
		jdbc.queryForObject("select 1 from plataforma.cambiar_mi_contrasena(?, ?)", Integer.class, actual, nueva);
	}

}
