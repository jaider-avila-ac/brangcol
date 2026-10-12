package co.brangcol.producto.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.producto.model.FiltroItems;
import co.brangcol.producto.model.PaginaItems;
import co.brangcol.producto.repository.ItemBusquedaRepository;
import co.brangcol.producto.service.ItemBusquedaService;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class ItemBusquedaServiceImpl implements ItemBusquedaService {

	private final ItemBusquedaRepository itemBusquedaRepository;

	private final VerificadorPermisos permisos;

	public ItemBusquedaServiceImpl(ItemBusquedaRepository itemBusquedaRepository, VerificadorPermisos permisos) {
		this.itemBusquedaRepository = itemBusquedaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaItems buscar(FiltroItems filtro) {
		permisos.exigir("productos:item:consultar");
		return itemBusquedaRepository.buscar(filtro);
	}

}
