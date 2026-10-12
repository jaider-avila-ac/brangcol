package co.brangcol.archivo.controller;

import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.archivo.model.ArchivoLeido;
import co.brangcol.archivo.service.ArchivoService;

@RestController
@RequestMapping("/api/v1/archivos")
public class ArchivoController {

	private final ArchivoService archivoService;

	public ArchivoController(ArchivoService archivoService) {
		this.archivoService = archivoService;
	}

	// Cada version de una imagen tiene su propio codigo, por eso se puede guardar en cache
	@GetMapping("/{codigo}")
	public ResponseEntity<byte[]> leer(@PathVariable String codigo) {
		ArchivoLeido archivo = archivoService.leer(codigo);
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(archivo.tipoMime()))
			.cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePrivate())
			.header("X-Content-Type-Options", "nosniff")
			.body(archivo.contenido());
	}

}
