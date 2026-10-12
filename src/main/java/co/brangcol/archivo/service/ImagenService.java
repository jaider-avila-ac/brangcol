package co.brangcol.archivo.service;

import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.model.ImagenGuardada;

public interface ImagenService {

	ImagenGuardada guardar(MultipartFile archivo);

	void descartar(ImagenGuardada imagen);

}
