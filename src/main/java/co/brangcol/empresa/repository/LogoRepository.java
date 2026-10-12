package co.brangcol.empresa.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.archivo.model.ImagenGuardada;

@Repository
public class LogoRepository {

	private final JdbcTemplate jdbc;

	public LogoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String registrar(ImagenGuardada imagen) {
		return jdbc.queryForObject("select plataforma.registrar_logo(?, ?, ?, ?, ?)", String.class,
				imagen.nombreOriginal(), imagen.ruta(), imagen.tipoMime(), imagen.tamano(), imagen.sha256());
	}

}
