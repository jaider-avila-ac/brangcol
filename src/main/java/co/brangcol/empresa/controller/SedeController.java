package co.brangcol.empresa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.empresa.dto.SedeRequest;
import co.brangcol.empresa.dto.SedeResponse;
import co.brangcol.empresa.mapper.SedeMapper;
import co.brangcol.empresa.service.SedeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/empresa/sedes")
public class SedeController {

	private final SedeService sedeService;

	private final SedeMapper sedeMapper;

	public SedeController(SedeService sedeService, SedeMapper sedeMapper) {
		this.sedeService = sedeService;
		this.sedeMapper = sedeMapper;
	}

	@GetMapping
	public List<SedeResponse> listar() {
		return sedeMapper.aRespuestas(sedeService.listar());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SedeResponse crear(@Valid @RequestBody SedeRequest request) {
		return sedeMapper.aRespuesta(sedeService.crear(sedeMapper.aDatos(request)));
	}

	@PutMapping("/{codigo}")
	public SedeResponse actualizar(@PathVariable String codigo, @Valid @RequestBody SedeRequest request) {
		return sedeMapper.aRespuesta(sedeService.actualizar(codigo, sedeMapper.aDatos(request)));
	}

}
