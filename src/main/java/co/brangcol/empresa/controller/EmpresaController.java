package co.brangcol.empresa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.empresa.dto.EmpresaRequest;
import co.brangcol.empresa.dto.EmpresaResponse;
import co.brangcol.empresa.mapper.EmpresaMapper;
import co.brangcol.empresa.service.EmpresaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/empresa")
public class EmpresaController {

	private final EmpresaService empresaService;

	private final EmpresaMapper empresaMapper;

	public EmpresaController(EmpresaService empresaService, EmpresaMapper empresaMapper) {
		this.empresaService = empresaService;
		this.empresaMapper = empresaMapper;
	}

	@GetMapping
	public EmpresaResponse obtener() {
		return empresaMapper.aRespuesta(empresaService.obtener());
	}

	@PutMapping
	public EmpresaResponse actualizar(@Valid @RequestBody EmpresaRequest request) {
		return empresaMapper.aRespuesta(empresaService.actualizar(empresaMapper.aDatos(request)));
	}

}
