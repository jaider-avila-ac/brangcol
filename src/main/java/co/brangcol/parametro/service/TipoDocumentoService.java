package co.brangcol.parametro.service;

import java.util.List;

import co.brangcol.parametro.model.TipoDocumento;

public interface TipoDocumentoService {

	List<TipoDocumento> todos();

	TipoDocumento buscar(String codigoOAbreviatura);

	void validar(String codigo);

}
