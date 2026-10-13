package co.brangcol.producto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.producto.dto.CambiarEstadoItemRequest;
import co.brangcol.producto.dto.ItemRequest;
import co.brangcol.producto.dto.ItemResponse;
import co.brangcol.producto.dto.ItemResumenResponse;
import co.brangcol.producto.mapper.ItemMapper;
import co.brangcol.producto.mapper.ItemResumenMapper;
import co.brangcol.producto.model.FiltroItems;
import co.brangcol.producto.service.ItemBusquedaService;
import co.brangcol.producto.service.ItemService;
import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;

@RestController
@RequestMapping("/api/v1/productos")
public class ItemController {

	private final ItemService itemService;

	private final ItemBusquedaService itemBusquedaService;

	private final ItemMapper itemMapper;

	private final ItemResumenMapper itemResumenMapper;

	public ItemController(ItemService itemService, ItemBusquedaService itemBusquedaService, ItemMapper itemMapper,
			ItemResumenMapper itemResumenMapper) {
		this.itemService = itemService;
		this.itemBusquedaService = itemBusquedaService;
		this.itemMapper = itemMapper;
		this.itemResumenMapper = itemResumenMapper;
	}

	@GetMapping
	public PageResponse<ItemResumenResponse> buscar(@RequestParam(defaultValue = "") String texto,
			@RequestParam(defaultValue = "") String tipo, @RequestParam(defaultValue = "") String categoria,
			@RequestParam(defaultValue = "false") boolean soloComponentes,
			@RequestParam(defaultValue = "false") boolean soloBajoMinimo,
			@RequestParam(defaultValue = "false") boolean incluirInactivos,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano) {
		FiltroItems filtro = new FiltroItems(Textos.vacioANulo(texto), Textos.vacioANulo(tipo),
				Textos.vacioANulo(categoria), soloComponentes, soloBajoMinimo, incluirInactivos, Math.max(pagina, 0),
				Math.min(Math.max(tamano, 1), 100));
		return itemResumenMapper.aPagina(itemBusquedaService.buscar(filtro), filtro);
	}

	@GetMapping("/{codigo}")
	public ItemResponse obtener(@PathVariable String codigo) {
		return itemMapper.aRespuesta(itemService.obtener(codigo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemResponse crear(@RequestBody ItemRequest request) {
		return itemMapper.aRespuesta(itemService.crear(itemMapper.aDatos(request)));
	}

	@PutMapping("/{codigo}")
	public ItemResponse actualizar(@PathVariable String codigo, @RequestBody ItemRequest request) {
		return itemMapper.aRespuesta(itemService.actualizar(codigo, itemMapper.aDatos(request)));
	}

	@PutMapping("/{codigo}/estado")
	public ItemResponse cambiarEstado(@PathVariable String codigo, @RequestBody CambiarEstadoItemRequest request) {
		return itemMapper.aRespuesta(itemService.cambiarEstado(codigo, request.activo()));
	}

}
