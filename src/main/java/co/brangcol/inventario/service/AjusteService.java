package co.brangcol.inventario.service;

import co.brangcol.inventario.model.Ajuste;
import co.brangcol.inventario.model.AjusteResumen;
import co.brangcol.inventario.model.NuevoAjuste;
import co.brangcol.inventario.model.Pagina;

public interface AjusteService {

	Ajuste registrar(NuevoAjuste ajuste);

	Pagina<AjusteResumen> listar(int pagina, int tamano);

	Ajuste obtener(String codigo);

	Ajuste anular(String codigo, String motivo);

}
