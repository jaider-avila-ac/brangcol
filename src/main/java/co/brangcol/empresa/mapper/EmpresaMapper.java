package co.brangcol.empresa.mapper;

import org.springframework.stereotype.Component;

import co.brangcol.empresa.dto.EmpresaRequest;
import co.brangcol.empresa.dto.EmpresaResponse;
import co.brangcol.empresa.model.DatosEmpresa;
import co.brangcol.empresa.model.Empresa;

@Component
public class EmpresaMapper {

	public EmpresaResponse aRespuesta(Empresa empresa) {
		return new EmpresaResponse(empresa.codigo(), empresa.tipoPersona(), empresa.razonSocial(),
				empresa.nombreComercial(), empresa.tipoDocumento(), empresa.numeroDocumento(), empresa.dv(),
				empresa.responsableIva(), empresa.direccion(),
				new EmpresaResponse.Municipio(empresa.municipioCodigo(), empresa.municipio(), empresa.departamento()),
				empresa.telefono(), empresa.correo(), empresa.estado());
	}

	public DatosEmpresa aDatos(EmpresaRequest request) {
		return new DatosEmpresa(request.razonSocial().trim(), Textos.vacioANulo(request.nombreComercial()),
				request.responsableIva(), request.direccion().trim(), request.municipio(),
				Textos.vacioANulo(request.telefono()), request.correo().trim().toLowerCase());
	}

}
