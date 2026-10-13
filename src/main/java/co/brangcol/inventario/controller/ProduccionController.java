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

import co.brangcol.inventario.dto.AnularRequest;
import co.brangcol.inventario.dto.ProduccionRequest;
import co.brangcol.inventario.dto.ProduccionResponse;
import co.brangcol.inventario.mapper.ProduccionMapper;
import co.brangcol.inventario.service.ProduccionService;
import co.brangcol.shared.web.PageResponse;

@RestController
@RequestMapping("/api/v1/inventario/producciones")
public class ProduccionController {

	private final ProduccionService produccionService;

	private final ProduccionMapper produccionMapper;

	public ProduccionController(ProduccionService produccionService, ProduccionMapper produccionMapper) {
		this.produccionService = produccionService;
		this.produccionMapper = produccionMapper;
	}

	@GetMapping
	public PageResponse<ProduccionResponse> listar(@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamano) {
		int numero = Math.max(pagina, 0);
		int tam = Math.min(Math.max(tamano, 1), 100);
		return produccionMapper.aPagina(produccionService.listar(numero, tam), numero, tam);
	}

	@GetMapping("/{codigo}")
	public ProduccionResponse obtener(@PathVariable String codigo) {
		return produccionMapper.aRespuesta(produccionService.obtener(codigo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProduccionResponse registrar(@RequestBody ProduccionRequest request) {
		return produccionMapper.aRespuesta(produccionService.registrar(produccionMapper.aNueva(request)));
	}

	@PostMapping("/{codigo}/anular")
	public ProduccionResponse anular(@PathVariable String codigo, @RequestBody AnularRequest request) {
		return produccionMapper.aRespuesta(produccionService.anular(codigo, request.motivo()));
	}

}
