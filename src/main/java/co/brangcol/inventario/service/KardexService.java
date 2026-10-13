package co.brangcol.inventario.service;

import co.brangcol.inventario.model.FiltroKardex;
import co.brangcol.inventario.model.Movimiento;
import co.brangcol.inventario.model.Pagina;

public interface KardexService {

	Pagina<Movimiento> buscar(FiltroKardex filtro);

}
