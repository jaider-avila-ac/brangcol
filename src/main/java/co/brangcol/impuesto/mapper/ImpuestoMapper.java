package co.brangcol.impuesto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.impuesto.dto.ImpuestoResponse;
import co.brangcol.impuesto.model.Impuesto;

@Component
public class ImpuestoMapper {

	public List<ImpuestoResponse> aRespuestas(List<Impuesto> impuestos) {
		List<ImpuestoResponse> respuesta = new ArrayList<>();
		for (Impuesto i : impuestos) {
			respuesta.add(new ImpuestoResponse(i.codigo(), i.nombre(), i.tipo(), i.tarifa()));
		}
		return respuesta;
	}

}
