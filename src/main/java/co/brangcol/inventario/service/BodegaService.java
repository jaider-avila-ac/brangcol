package co.brangcol.inventario.service;

import java.util.List;

import co.brangcol.inventario.model.Bodega;
import co.brangcol.inventario.model.DatosBodega;

public interface BodegaService {

	List<Bodega> listar();

	List<Bodega> crear(DatosBodega datos);

	List<Bodega> actualizar(String codigo, DatosBodega datos);

}
