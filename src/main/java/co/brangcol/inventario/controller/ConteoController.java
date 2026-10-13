package co.brangcol.inventario.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.inventario.dto.ConteoRequest;
import co.brangcol.inventario.dto.ConteoResponse;
import co.brangcol.inventario.mapper.LineaMapper;
import co.brangcol.inventario.model.NuevoConteo;
import co.brangcol.inventario.service.ConteoService;
import co.brangcol.shared.texto.Textos;

@RestController
@RequestMapping("/api/v1/inventario/conteos")
public class ConteoController {

	private final ConteoService conteoService;

	private final LineaMapper lineaMapper;

	public ConteoController(ConteoService conteoService, LineaMapper lineaMapper) {
		this.conteoService = conteoService;
		this.lineaMapper = lineaMapper;
	}

	@PostMapping
	public ConteoResponse registrar(@RequestBody ConteoRequest request) {
		NuevoConteo conteo = new NuevoConteo(Textos.vacioANulo(request.bodega()), request.observacion(),
				lineaMapper.aLineas(request.lineas()));
		return new ConteoResponse(conteoService.registrar(conteo));
	}

}
