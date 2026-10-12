package co.brangcol.producto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.producto.dto.RecetaRequest;
import co.brangcol.producto.dto.RecetaResponse;
import co.brangcol.producto.mapper.RecetaMapper;
import co.brangcol.producto.service.RecetaService;

@RestController
@RequestMapping("/api/v1/productos/{codigo}/receta")
public class RecetaController {

	private final RecetaService recetaService;

	private final RecetaMapper recetaMapper;

	public RecetaController(RecetaService recetaService, RecetaMapper recetaMapper) {
		this.recetaService = recetaService;
		this.recetaMapper = recetaMapper;
	}

	@GetMapping
	public RecetaResponse obtener(@PathVariable String codigo) {
		return recetaMapper.aRespuesta(recetaService.obtener(codigo));
	}

	@PutMapping
	public RecetaResponse guardar(@PathVariable String codigo, @RequestBody RecetaRequest request) {
		return recetaMapper.aRespuesta(recetaService.guardar(codigo, recetaMapper.aComponentes(request)));
	}

}
