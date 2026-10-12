package co.brangcol.producto.service;

import java.util.List;

import co.brangcol.producto.model.ComponenteReceta;
import co.brangcol.producto.model.Receta;

public interface RecetaService {

	Receta obtener(String itemCodigo);

	Receta guardar(String itemCodigo, List<ComponenteReceta> componentes);

}
