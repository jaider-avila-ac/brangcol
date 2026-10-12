package co.brangcol.empresa.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.empresa.dto.EmpresaResponse;
import co.brangcol.empresa.dto.SedeRequest;
import co.brangcol.empresa.dto.SedeResponse;
import co.brangcol.empresa.model.DatosSede;
import co.brangcol.empresa.model.Sede;

@Component
public class SedeMapper {

	public SedeResponse aRespuesta(Sede sede) {
		return new SedeResponse(sede.codigo(), sede.nombre(), sede.direccion(),
				new EmpresaResponse.Municipio(sede.municipioCodigo(), sede.municipio(), null), sede.telefono(),
				sede.esPrincipal(), sede.activa());
	}

	public List<SedeResponse> aRespuestas(List<Sede> sedes) {
		List<SedeResponse> respuesta = new ArrayList<>();
		for (Sede sede : sedes) {
			respuesta.add(aRespuesta(sede));
		}
		return respuesta;
	}

	public DatosSede aDatos(SedeRequest request) {
		return new DatosSede(request.nombre().trim(), request.direccion().trim(), request.municipio(),
				Textos.vacioANulo(request.telefono()), request.activa());
	}

}
