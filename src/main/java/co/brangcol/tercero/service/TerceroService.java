package co.brangcol.tercero.service;

import co.brangcol.tercero.model.Coincidencia;
import co.brangcol.tercero.model.DatosTercero;
import co.brangcol.tercero.model.Tercero;

public interface TerceroService {

	Tercero crear(DatosTercero datos);

	Tercero actualizar(String codigo, DatosTercero datos);

	Tercero obtener(String codigo);

	Tercero cambiarEstado(String codigo, boolean activo);

	Coincidencia buscarPorDocumento(String tipoDocumento, String numero);

	boolean agregarRoles(String codigo, DatosTercero datos);

}
