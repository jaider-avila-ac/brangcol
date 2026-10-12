package co.brangcol.producto.service;

import co.brangcol.producto.model.FiltroItems;
import co.brangcol.producto.model.PaginaItems;

public interface ItemBusquedaService {

	PaginaItems buscar(FiltroItems filtro);

}
