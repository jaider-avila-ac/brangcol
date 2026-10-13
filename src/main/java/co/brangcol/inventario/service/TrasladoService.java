package co.brangcol.inventario.service;

import java.util.List;

import co.brangcol.inventario.model.LineaInventario;
import co.brangcol.inventario.model.NuevoTraslado;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Traslado;
import co.brangcol.inventario.model.TrasladoResumen;

public interface TrasladoService {

	Traslado registrar(NuevoTraslado traslado);

	Pagina<TrasladoResumen> listar(int pagina, int tamano);

	Traslado obtener(String codigo);

	Traslado recibir(String codigo, List<LineaInventario> recibidos);

	Traslado anular(String codigo, String motivo);

}
