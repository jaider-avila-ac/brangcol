package co.brangcol.shared.permiso;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import co.brangcol.shared.error.NegocioException;

// Las escrituras ya las protege la base; esto cubre las consultas
@Component
public class VerificadorPermisos {

	private final JdbcTemplate jdbc;

	public VerificadorPermisos(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public boolean tiene(String permiso) {
		Boolean tiene = jdbc.queryForObject("select plataforma.tiene_permiso(?)", Boolean.class, permiso);
		return Boolean.TRUE.equals(tiene);
	}

	public void exigir(String permiso) {
		if (!tiene(permiso)) {
			throw new NegocioException(HttpStatus.FORBIDDEN, "No tiene permiso para esta operación");
		}
	}

}
