package co.brangcol.tercero.service;

import co.brangcol.tercero.model.FiltroTerceros;
import co.brangcol.tercero.model.PaginaTerceros;

public interface TerceroBusquedaService {

	PaginaTerceros buscar(FiltroTerceros filtro);

}
