package co.brangcol.producto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.producto.dto.MarcaResponse;
import co.brangcol.producto.model.Marca;

@Component
public class MarcaMapper {

	public List<MarcaResponse> aRespuestas(List<Marca> marcas) {
		List<MarcaResponse> respuesta = new ArrayList<>();
		for (Marca m : marcas) {
			respuesta.add(new MarcaResponse(m.codigo(), m.nombre(), m.activa()));
		}
		return respuesta;
	}

}
