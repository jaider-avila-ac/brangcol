package co.brangcol.inventario.service;

import co.brangcol.inventario.model.NuevaProduccion;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Produccion;

public interface ProduccionService {

	Produccion registrar(NuevaProduccion produccion);

	Pagina<Produccion> listar(int pagina, int tamano);

	Produccion obtener(String codigo);

	Produccion anular(String codigo, String motivo);

}
