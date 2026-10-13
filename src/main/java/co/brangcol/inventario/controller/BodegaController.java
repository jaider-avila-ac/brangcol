package co.brangcol.inventario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.inventario.dto.BodegaRequest;
import co.brangcol.inventario.dto.BodegaResponse;
import co.brangcol.inventario.mapper.BodegaMapper;
import co.brangcol.inventario.service.BodegaService;

@RestController
@RequestMapping("/api/v1/inventario/bodegas")
public class BodegaController {

	private final BodegaService bodegaService;

	private final BodegaMapper bodegaMapper;

	public BodegaController(BodegaService bodegaService, BodegaMapper bodegaMapper) {
		this.bodegaService = bodegaService;
		this.bodegaMapper = bodegaMapper;
	}

	@GetMapping
	public List<BodegaResponse> listar() {
		return bodegaMapper.aRespuestas(bodegaService.listar());
	}

	@PostMapping
	public List<BodegaResponse> crear(@RequestBody BodegaRequest request) {
		return bodegaMapper.aRespuestas(bodegaService.crear(bodegaMapper.aDatos(request)));
	}

	@PutMapping("/{codigo}")
	public List<BodegaResponse> actualizar(@PathVariable String codigo, @RequestBody BodegaRequest request) {
		return bodegaMapper.aRespuestas(bodegaService.actualizar(codigo, bodegaMapper.aDatos(request)));
	}

}
