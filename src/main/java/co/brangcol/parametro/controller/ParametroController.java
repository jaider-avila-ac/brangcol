package co.brangcol.parametro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.parametro.dto.MunicipioResponse;
import co.brangcol.parametro.mapper.ParametroMapper;
import co.brangcol.parametro.service.ParametroService;

@RestController
@RequestMapping("/api/v1/parametros")
public class ParametroController {

	private final ParametroService parametroService;

	private final ParametroMapper parametroMapper;

	public ParametroController(ParametroService parametroService, ParametroMapper parametroMapper) {
		this.parametroService = parametroService;
		this.parametroMapper = parametroMapper;
	}

	@GetMapping("/municipios")
	public List<MunicipioResponse> municipios(@RequestParam(defaultValue = "") String buscar) {
		return parametroMapper.aMunicipios(parametroService.buscarMunicipios(buscar));
	}

}
