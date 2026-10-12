package co.brangcol.tercero.controller;

import java.io.IOException;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.shared.error.NegocioException;
import co.brangcol.tercero.dto.ResultadoImportacionResponse;
import co.brangcol.tercero.mapper.ImportacionMapper;
import co.brangcol.tercero.service.ImportacionTercerosService;
import co.brangcol.tercero.service.PlantillaTercerosService;

@RestController
@RequestMapping("/api/v1/terceros")
public class ImportacionTercerosController {

	private static final MediaType EXCEL = MediaType
		.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private final ImportacionTercerosService importacionService;

	private final PlantillaTercerosService plantillaService;

	private final ImportacionMapper importacionMapper;

	public ImportacionTercerosController(ImportacionTercerosService importacionService,
			PlantillaTercerosService plantillaService, ImportacionMapper importacionMapper) {
		this.importacionService = importacionService;
		this.plantillaService = plantillaService;
		this.importacionMapper = importacionMapper;
	}

	@GetMapping("/plantilla")
	public ResponseEntity<byte[]> plantilla() {
		return ResponseEntity.ok()
			.contentType(EXCEL)
			.header(HttpHeaders.CONTENT_DISPOSITION,
					ContentDisposition.attachment().filename("plantilla_clientes_proveedores.xlsx").build().toString())
			.body(plantillaService.generar());
	}

	@PostMapping("/importar")
	public ResultadoImportacionResponse importar(@RequestParam("archivo") MultipartFile archivo) throws IOException {
		if (archivo.isEmpty()) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Seleccione un archivo");
		}
		return importacionMapper.aRespuesta(importacionService.importar(archivo.getInputStream()));
	}

}
