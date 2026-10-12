package co.brangcol.parametro.service;

import java.util.List;

import co.brangcol.parametro.model.Municipio;

public interface ParametroService {

	List<Municipio> buscarMunicipios(String texto);

	void validarMunicipio(String codigo);

}
