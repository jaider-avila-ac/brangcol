package co.brangcol.inventario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.inventario.dto.ExistenciaResponse;
import co.brangcol.inventario.dto.MinimoRequest;
import co.brangcol.inventario.dto.ResumenInventarioResponse;
import co.brangcol.inventario.mapper.ExistenciaMapper;
import co.brangcol.inventario.model.FiltroExistencias;
import co.brangcol.inventario.service.ExistenciaService;
import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;

@RestController
@RequestMapping("/api/v1/inventario/existencias")
public class ExistenciaController {

	private final ExistenciaService existenciaService;

	private final ExistenciaMapper existenciaMapper;

	public ExistenciaController(ExistenciaService existenciaService, ExistenciaMapper existenciaMapper) {
		this.existenciaService = existenciaService;
		this.existenciaMapper = existenciaMapper;
	}

	@GetMapping
	public PageResponse<ExistenciaResponse> buscar(@RequestParam(defaultValue = "") String texto,
			@RequestParam(defaultValue = "") String bodega,
			@RequestParam(defaultValue = "false") boolean soloBajoMinimo,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano) {
		FiltroExistencias filtro = new FiltroExistencias(Textos.vacioANulo(texto), Textos.vacioANulo(bodega),
				soloBajoMinimo, Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 200));
		return existenciaMapper.aPagina(existenciaService.buscar(filtro), filtro);
	}

	@GetMapping("/resumen")
	public ResumenInventarioResponse resumen() {
		return existenciaMapper.aRespuesta(existenciaService.resumen());
	}

	@PutMapping("/minimo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void definirMinimo(@RequestBody MinimoRequest request) {
		existenciaService.definirMinimo(request.bodega(), request.item(), request.minimo());
	}

}
