package co.brangcol.archivo.service.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Guarda y lee los bytes en disco; la base solo conoce la ruta relativa
@Component
public class AlmacenDisco {

	private final Path raiz;

	public AlmacenDisco(@Value("${brangcol.archivos.directorio}") String directorio) {
		this.raiz = Path.of(directorio).toAbsolutePath().normalize();
	}

	public String guardar(byte[] contenido, String extension) {
		LocalDate hoy = LocalDate.now();
		String ruta = hoy.getYear() + "/" + String.format("%02d", hoy.getMonthValue()) + "/" + UUID.randomUUID() + "."
				+ extension;
		Path destino = resolver(ruta);
		try {
			Files.createDirectories(destino.getParent());
			Files.write(destino, contenido);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		return ruta;
	}

	public byte[] leer(String ruta) {
		try {
			return Files.readAllBytes(resolver(ruta));
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	public void borrar(String ruta) {
		try {
			Files.deleteIfExists(resolver(ruta));
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private Path resolver(String ruta) {
		Path destino = raiz.resolve(ruta).normalize();
		if (!destino.startsWith(raiz)) {
			throw new IllegalArgumentException("Ruta de archivo no permitida");
		}
		return destino;
	}

}
