package co.brangcol.auth.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.archivo.model.ImagenGuardada;

@Repository
public class MiFotoRepository {

	private final JdbcTemplate jdbc;

	public MiFotoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String registrar(ImagenGuardada imagen) {
		return jdbc.queryForObject("select plataforma.registrar_mi_foto(?, ?, ?, ?, ?)", String.class,
				imagen.nombreOriginal(), imagen.ruta(), imagen.tipoMime(), imagen.tamano(), imagen.sha256());
	}

}
