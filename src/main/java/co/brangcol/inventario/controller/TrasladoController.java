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
import co.brangcol.inventario.dto.RecibirRequest;
import co.brangcol.inventario.dto.TrasladoRequest;
import co.brangcol.inventario.dto.TrasladoResponse;
import co.brangcol.inventario.dto.TrasladoResumenResponse;
import co.brangcol.inventario.mapper.LineaMapper;
import co.brangcol.inventario.mapper.TrasladoMapper;
import co.brangcol.inventario.service.TrasladoService;
import co.brangcol.shared.web.PageResponse;

@RestController
@RequestMapping("/api/v1/inventario/traslados")
public class TrasladoController {

	private final TrasladoService trasladoService;

	private final TrasladoMapper trasladoMapper;

	private final LineaMapper lineaMapper;

	public TrasladoController(TrasladoService trasladoService, TrasladoMapper trasladoMapper,
			LineaMapper lineaMapper) {
		this.trasladoService = trasladoService;
		this.trasladoMapper = trasladoMapper;
		this.lineaMapper = lineaMapper;
	}

	@GetMapping
	public PageResponse<TrasladoResumenResponse> listar(@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamano) {
		int numero = Math.max(pagina, 0);
		int tam = Math.min(Math.max(tamano, 1), 100);
		return trasladoMapper.aPagina(trasladoService.listar(numero, tam), numero, tam);
	}

	@GetMapping("/{codigo}")
	public TrasladoResponse obtener(@PathVariable String codigo) {
		return trasladoMapper.aRespuesta(trasladoService.obtener(codigo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TrasladoResponse registrar(@RequestBody TrasladoRequest request) {
		return trasladoMapper.aRespuesta(trasladoService.registrar(trasladoMapper.aNuevo(request)));
	}

	@PostMapping("/{codigo}/recibir")
	public TrasladoResponse recibir(@PathVariable String codigo, @RequestBody(required = false) RecibirRequest request) {
		return trasladoMapper.aRespuesta(
				trasladoService.recibir(codigo, request == null ? null : lineaMapper.aLineas(request.lineas())));
	}

	@PostMapping("/{codigo}/anular")
	public TrasladoResponse anular(@PathVariable String codigo, @RequestBody AnularRequest request) {
		return trasladoMapper.aRespuesta(trasladoService.anular(codigo, request.motivo()));
	}

}
