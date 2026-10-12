package co.brangcol.parametro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.parametro.dto.MunicipioResponse;
import co.brangcol.parametro.mapper.MunicipioMapper;
import co.brangcol.parametro.service.MunicipioService;

@RestController
@RequestMapping("/api/v1/parametros")
public class MunicipioController {

	private final MunicipioService municipioService;

	private final MunicipioMapper municipioMapper;

	public MunicipioController(MunicipioService municipioService, MunicipioMapper municipioMapper) {
		this.municipioService = municipioService;
		this.municipioMapper = municipioMapper;
	}

	@GetMapping("/municipios")
	public List<MunicipioResponse> municipios(@RequestParam(defaultValue = "") String buscar) {
		return municipioMapper.aMunicipios(municipioService.buscarMunicipios(buscar));
	}

}
