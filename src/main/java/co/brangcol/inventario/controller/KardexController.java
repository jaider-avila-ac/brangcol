package co.brangcol.inventario.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.inventario.dto.MovimientoResponse;
import co.brangcol.inventario.mapper.MovimientoMapper;
import co.brangcol.inventario.model.FiltroKardex;
import co.brangcol.inventario.service.KardexService;
import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;

@RestController
@RequestMapping("/api/v1/inventario/kardex")
public class KardexController {

	private final KardexService kardexService;

	private final MovimientoMapper movimientoMapper;

	public KardexController(KardexService kardexService, MovimientoMapper movimientoMapper) {
		this.kardexService = kardexService;
		this.movimientoMapper = movimientoMapper;
	}

	@GetMapping("/{item}")
	public PageResponse<MovimientoResponse> buscar(@PathVariable String item,
			@RequestParam(defaultValue = "") String bodega,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "50") int tamano) {
		FiltroKardex filtro = new FiltroKardex(item, Textos.vacioANulo(bodega), desde, hasta, Math.max(pagina, 0),
				Math.min(Math.max(tamano, 1), 200));
		return movimientoMapper.aPagina(kardexService.buscar(filtro), filtro);
	}

}
