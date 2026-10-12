package co.brangcol.empresa.service;

import co.brangcol.empresa.model.ConfiguracionEmpresa;

public interface ConfiguracionService {

	ConfiguracionEmpresa obtener();

	ConfiguracionEmpresa actualizar(ConfiguracionEmpresa configuracion);

}
