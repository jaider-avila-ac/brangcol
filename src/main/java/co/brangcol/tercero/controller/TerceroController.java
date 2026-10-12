package co.brangcol.tercero.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;
import co.brangcol.tercero.dto.CambiarEstadoTerceroRequest;
import co.brangcol.tercero.dto.CoincidenciaResponse;
import co.brangcol.tercero.dto.TerceroRequest;
import co.brangcol.tercero.dto.TerceroResponse;
import co.brangcol.tercero.dto.TerceroResumenResponse;
import co.brangcol.tercero.mapper.TerceroMapper;
import co.brangcol.tercero.mapper.TerceroResumenMapper;
import co.brangcol.tercero.model.Coincidencia;
import co.brangcol.tercero.model.FiltroTerceros;
import co.brangcol.tercero.model.RolTercero;
import co.brangcol.tercero.service.TerceroBusquedaService;
import co.brangcol.tercero.service.TerceroService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/terceros")
public class TerceroController {

	private final TerceroService terceroService;

	private final TerceroBusquedaService terceroBusquedaService;

	private final TerceroMapper terceroMapper;

	private final TerceroResumenMapper terceroResumenMapper;

	public TerceroController(TerceroService terceroService, TerceroBusquedaService terceroBusquedaService,
			TerceroMapper terceroMapper, TerceroResumenMapper terceroResumenMapper) {
		this.terceroService = terceroService;
		this.terceroBusquedaService = terceroBusquedaService;
		this.terceroMapper = terceroMapper;
		this.terceroResumenMapper = terceroResumenMapper;
	}

	@GetMapping
	public PageResponse<TerceroResumenResponse> buscar(@RequestParam(defaultValue = "") String texto,
			@RequestParam(defaultValue = "TODOS") RolTercero rol,
			@RequestParam(defaultValue = "false") boolean soloDeben,
			@RequestParam(defaultValue = "false") boolean incluirInactivos,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano) {
		FiltroTerceros filtro = new FiltroTerceros(Textos.vacioANulo(texto), rol, soloDeben, incluirInactivos,
				Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 100));
		return terceroResumenMapper.aPagina(terceroBusquedaService.buscar(filtro), filtro);
	}

	@GetMapping("/documento")
	public ResponseEntity<CoincidenciaResponse> porDocumento(@RequestParam String tipo, @RequestParam String numero) {
		Coincidencia coincidencia = terceroService.buscarPorDocumento(tipo, Textos.documento(numero));
		if (coincidencia == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(terceroResumenMapper.aCoincidencia(coincidencia));
	}

	@GetMapping("/{codigo}")
	public TerceroResponse obtener(@PathVariable String codigo) {
		return terceroMapper.aRespuesta(terceroService.obtener(codigo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TerceroResponse crear(@Valid @RequestBody TerceroRequest request) {
		return terceroMapper.aRespuesta(terceroService.crear(terceroMapper.aDatos(request)));
	}

	@PutMapping("/{codigo}")
	public TerceroResponse actualizar(@PathVariable String codigo, @Valid @RequestBody TerceroRequest request) {
		return terceroMapper.aRespuesta(terceroService.actualizar(codigo, terceroMapper.aDatos(request)));
	}

	@PutMapping("/{codigo}/estado")
	public TerceroResponse cambiarEstado(@PathVariable String codigo,
			@RequestBody CambiarEstadoTerceroRequest request) {
		return terceroMapper.aRespuesta(terceroService.cambiarEstado(codigo, request.activo()));
	}

}
