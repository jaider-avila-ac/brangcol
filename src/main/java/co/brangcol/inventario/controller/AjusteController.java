package co.brangcol.inventario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.inventario.dto.AjusteRequest;
import co.brangcol.inventario.dto.AjusteResponse;
import co.brangcol.inventario.dto.AjusteResumenResponse;
import co.brangcol.inventario.dto.AnularRequest;
import co.brangcol.inventario.mapper.AjusteMapper;
import co.brangcol.inventario.service.AjusteService;
import co.brangcol.shared.web.PageResponse;

@RestController
@RequestMapping("/api/v1/inventario/ajustes")
public class AjusteController {

	private final AjusteService ajusteService;

	private final AjusteMapper ajusteMapper;

	public AjusteController(AjusteService ajusteService, AjusteMapper ajusteMapper) {
		this.ajusteService = ajusteService;
		this.ajusteMapper = ajusteMapper;
	}

	@GetMapping
	public PageResponse<AjusteResumenResponse> listar(@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamano) {
		int numero = Math.max(pagina, 0);
		int tam = Math.min(Math.max(tamano, 1), 100);
		return ajusteMapper.aPagina(ajusteService.listar(numero, tam), numero, tam);
	}

	@GetMapping("/{codigo}")
	public AjusteResponse obtener(@PathVariable String codigo) {
		return ajusteMapper.aRespuesta(ajusteService.obtener(codigo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AjusteResponse registrar(@RequestBody AjusteRequest request) {
		return ajusteMapper.aRespuesta(ajusteService.registrar(ajusteMapper.aNuevo(request)));
	}

	@PostMapping("/{codigo}/anular")
	public AjusteResponse anular(@PathVariable String codigo, @RequestBody AnularRequest request) {
		return ajusteMapper.aRespuesta(ajusteService.anular(codigo, request.motivo()));
	}

}
