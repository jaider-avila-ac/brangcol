package co.brangcol.tercero.service;

import java.io.InputStream;

import co.brangcol.tercero.model.ResultadoImportacion;

public interface ImportacionTercerosService {

	ResultadoImportacion importar(InputStream archivo);

}
