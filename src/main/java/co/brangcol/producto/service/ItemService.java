package co.brangcol.producto.service;

import co.brangcol.producto.model.DatosItem;
import co.brangcol.producto.model.Item;

public interface ItemService {

	Item crear(DatosItem datos);

	Item actualizar(String codigo, DatosItem datos);

	Item obtener(String codigo);

	Item cambiarEstado(String codigo, boolean activo);

}
