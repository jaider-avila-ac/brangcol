package co.brangcol.producto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.producto.dto.RecetaResumenResponse;
import co.brangcol.producto.model.RecetaResumen;

@Component
public class RecetaResumenMapper {

	public List<RecetaResumenResponse> aRespuestas(List<RecetaResumen> recetas) {
		List<RecetaResumenResponse> respuestas = new ArrayList<>();
		for (RecetaResumen r : recetas) {
			respuestas.add(new RecetaResumenResponse(r.codigo(), r.sku(), r.nombre(), r.tipo(), r.componentes(),
					r.cantidadComponentes(), r.costo(), r.precioConImpuesto()));
		}
		return respuestas;
	}

}
