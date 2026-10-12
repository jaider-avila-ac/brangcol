package co.brangcol.parametro.service;

import java.util.List;

import co.brangcol.parametro.model.Unidad;

public interface UnidadService {

	List<Unidad> todas();

	Unidad buscar(String codigo);

}
