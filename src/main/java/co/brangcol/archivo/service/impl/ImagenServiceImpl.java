package co.brangcol.archivo.service.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.model.ImagenGuardada;
import co.brangcol.archivo.service.ImagenService;
import co.brangcol.shared.error.NegocioException;

@Service
public class ImagenServiceImpl implements ImagenService {

	private static final long MAXIMO_BYTES = 3L * 1024 * 1024;

	private final AlmacenDisco almacen;

	public ImagenServiceImpl(AlmacenDisco almacen) {
		this.almacen = almacen;
	}

	@Override
	public ImagenGuardada guardar(MultipartFile archivo) {
		if (archivo == null || archivo.isEmpty()) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Seleccione una imagen");
		}
		if (archivo.getSize() > MAXIMO_BYTES) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "La imagen no puede pesar más de 3 MB");
		}
		byte[] contenido = leer(archivo);
		TipoImagen tipo = TipoImagen.detectar(contenido);
		if (tipo == null) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Solo se permiten imágenes PNG, JPG o WEBP");
		}
		String ruta = almacen.guardar(contenido, tipo.extension());
		String nombre = archivo.getOriginalFilename() == null ? "imagen." + tipo.extension()
				: archivo.getOriginalFilename();
		return new ImagenGuardada(nombre, ruta, tipo.mime(), contenido.length, sha256(contenido));
	}

	@Override
	public void descartar(ImagenGuardada imagen) {
		almacen.borrar(imagen.ruta());
	}

	private byte[] leer(MultipartFile archivo) {
		try {
			return archivo.getBytes();
		}
		catch (IOException ex) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "No se pudo leer la imagen");
		}
	}

	private byte[] sha256(byte[] contenido) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(contenido);
		}
		catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
