package co.brangcol.empresa.service;

import org.springframework.web.multipart.MultipartFile;

public interface LogoService {

	String subir(MultipartFile archivo);

}
