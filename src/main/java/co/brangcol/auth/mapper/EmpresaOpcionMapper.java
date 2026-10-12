package co.brangcol.auth.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.auth.dto.EmpresaOpcionResponse;
import co.brangcol.auth.model.EmpresaDisponible;

@Component
public class EmpresaOpcionMapper {

	public List<EmpresaOpcionResponse> aOpciones(List<EmpresaDisponible> empresas) {
		List<EmpresaOpcionResponse> respuesta = new ArrayList<>();
		for (EmpresaDisponible empresa : empresas) {
			respuesta.add(new EmpresaOpcionResponse(empresa.codigo(), empresa.razonSocial(), empresa.rol()));
		}
		return respuesta;
	}

}
