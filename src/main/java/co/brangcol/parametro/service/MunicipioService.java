package co.brangcol.parametro.service;

import java.util.List;

import co.brangcol.parametro.model.Municipio;

public interface MunicipioService {

	List<Municipio> buscarMunicipios(String texto);

	List<Municipio> todos();

	void validarMunicipio(String codigo);

}
