package co.brangcol.empresa.service;

import co.brangcol.empresa.model.DatosEmpresa;
import co.brangcol.empresa.model.Empresa;

public interface EmpresaService {

	Empresa obtener();

	Empresa actualizar(DatosEmpresa datos);

}
