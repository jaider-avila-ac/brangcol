package co.brangcol.producto.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.archivo.model.ImagenGuardada;

@Repository
public class FotoProductoRepository {

	private final JdbcTemplate jdbc;

	public FotoProductoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String registrar(String itemCodigo, ImagenGuardada imagen) {
		return jdbc.queryForObject("select producto.registrar_foto(?, ?, ?, ?, ?, ?)", String.class, itemCodigo,
				imagen.nombreOriginal(), imagen.ruta(), imagen.tipoMime(), imagen.tamano(), imagen.sha256());
	}

}
