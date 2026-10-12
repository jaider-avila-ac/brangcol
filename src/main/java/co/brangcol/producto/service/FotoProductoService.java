package co.brangcol.producto.service;

import org.springframework.web.multipart.MultipartFile;

public interface FotoProductoService {

	String subir(String itemCodigo, MultipartFile archivo);

}
