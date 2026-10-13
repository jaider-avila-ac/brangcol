package co.brangcol.producto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.producto.dto.RecetaResumenResponse;
import co.brangcol.producto.mapper.RecetaResumenMapper;
import co.brangcol.producto.service.RecetaResumenService;
import co.brangcol.shared.texto.Textos;

@RestController
@RequestMapping("/api/v1/productos/recetas")
public class RecetasController {

	private final RecetaResumenService recetaResumenService;

	private final RecetaResumenMapper recetaResumenMapper;

	public RecetasController(RecetaResumenService recetaResumenService, RecetaResumenMapper recetaResumenMapper) {
		this.recetaResumenService = recetaResumenService;
		this.recetaResumenMapper = recetaResumenMapper;
	}

	@GetMapping
	public List<RecetaResumenResponse> listar(@RequestParam(defaultValue = "") String texto) {
		return recetaResumenMapper.aRespuestas(recetaResumenService.listar(Textos.vacioANulo(texto)));
	}

}
