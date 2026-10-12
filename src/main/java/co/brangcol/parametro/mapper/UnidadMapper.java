package co.brangcol.parametro.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.parametro.dto.UnidadResponse;
import co.brangcol.parametro.model.Unidad;

@Component
public class UnidadMapper {

	public List<UnidadResponse> aRespuestas(List<Unidad> unidades) {
		List<UnidadResponse> respuesta = new ArrayList<>();
		for (Unidad u : unidades) {
			respuesta.add(new UnidadResponse(u.codigo(), u.nombre(), u.simbolo(), u.magnitud(), u.permiteFraccion()));
		}
		return respuesta;
	}

}
