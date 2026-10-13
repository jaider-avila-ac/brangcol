package co.brangcol.inventario.service;

import java.math.BigDecimal;

import co.brangcol.inventario.model.Existencia;
import co.brangcol.inventario.model.FiltroExistencias;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.ResumenInventario;

public interface ExistenciaService {

	Pagina<Existencia> buscar(FiltroExistencias filtro);

	ResumenInventario resumen();

	void definirMinimo(String bodega, String item, BigDecimal minimo);

}
