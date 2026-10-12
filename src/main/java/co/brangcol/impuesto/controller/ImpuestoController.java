package co.brangcol.impuesto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.impuesto.dto.ImpuestoResponse;
import co.brangcol.impuesto.mapper.ImpuestoMapper;
import co.brangcol.impuesto.service.ImpuestoService;

@RestController
@RequestMapping("/api/v1/impuestos")
public class ImpuestoController {

	private final ImpuestoService impuestoService;

	private final ImpuestoMapper impuestoMapper;

	public ImpuestoController(ImpuestoService impuestoService, ImpuestoMapper impuestoMapper) {
		this.impuestoService = impuestoService;
		this.impuestoMapper = impuestoMapper;
	}

	@GetMapping
	public List<ImpuestoResponse> listar() {
		return impuestoMapper.aRespuestas(impuestoService.activos());
	}

}
