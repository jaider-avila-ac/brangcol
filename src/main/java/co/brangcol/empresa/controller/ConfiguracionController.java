package co.brangcol.empresa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.empresa.dto.ConfiguracionDto;
import co.brangcol.empresa.mapper.ConfiguracionMapper;
import co.brangcol.empresa.service.ConfiguracionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/empresa/configuracion")
public class ConfiguracionController {

	private final ConfiguracionService configuracionService;

	private final ConfiguracionMapper configuracionMapper;

	public ConfiguracionController(ConfiguracionService configuracionService,
			ConfiguracionMapper configuracionMapper) {
		this.configuracionService = configuracionService;
		this.configuracionMapper = configuracionMapper;
	}

	@GetMapping
	public ConfiguracionDto obtener() {
		return configuracionMapper.aDto(configuracionService.obtener());
	}

	@PutMapping
	public ConfiguracionDto actualizar(@Valid @RequestBody ConfiguracionDto request) {
		return configuracionMapper.aDto(configuracionService.actualizar(configuracionMapper.aModelo(request)));
	}

}
