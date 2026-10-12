package co.brangcol.parametro.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.parametro.dto.MunicipioResponse;
import co.brangcol.parametro.model.Municipio;

@Component
public class MunicipioMapper {

	public List<MunicipioResponse> aMunicipios(List<Municipio> municipios) {
		List<MunicipioResponse> respuesta = new ArrayList<>();
		for (Municipio municipio : municipios) {
			respuesta.add(new MunicipioResponse(municipio.codigo(), municipio.nombre(), municipio.departamento()));
		}
		return respuesta;
	}

}
