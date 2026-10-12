package co.brangcol.parametro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.parametro.dto.TipoDocumentoResponse;
import co.brangcol.parametro.mapper.TipoDocumentoMapper;
import co.brangcol.parametro.service.TipoDocumentoService;

@RestController
@RequestMapping("/api/v1/parametros/tipos-documento")
public class TipoDocumentoController {

	private final TipoDocumentoService tipoDocumentoService;

	private final TipoDocumentoMapper tipoDocumentoMapper;

	public TipoDocumentoController(TipoDocumentoService tipoDocumentoService, TipoDocumentoMapper tipoDocumentoMapper) {
		this.tipoDocumentoService = tipoDocumentoService;
		this.tipoDocumentoMapper = tipoDocumentoMapper;
	}

	@GetMapping
	public List<TipoDocumentoResponse> listar() {
		return tipoDocumentoMapper.aRespuestas(tipoDocumentoService.todos());
	}

}
