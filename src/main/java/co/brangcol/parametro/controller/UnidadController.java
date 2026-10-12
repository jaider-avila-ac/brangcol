package co.brangcol.parametro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.parametro.dto.UnidadResponse;
import co.brangcol.parametro.mapper.UnidadMapper;
import co.brangcol.parametro.service.UnidadService;

@RestController
@RequestMapping("/api/v1/parametros/unidades")
public class UnidadController {

	private final UnidadService unidadService;

	private final UnidadMapper unidadMapper;

	public UnidadController(UnidadService unidadService, UnidadMapper unidadMapper) {
		this.unidadService = unidadService;
		this.unidadMapper = unidadMapper;
	}

	@GetMapping
	public List<UnidadResponse> listar() {
		return unidadMapper.aRespuestas(unidadService.todas());
	}

}
