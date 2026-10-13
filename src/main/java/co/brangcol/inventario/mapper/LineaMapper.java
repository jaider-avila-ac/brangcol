package co.brangcol.inventario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.LineaRequest;
import co.brangcol.inventario.dto.LineaResponse;
import co.brangcol.inventario.model.LineaInventario;

@Component
public class LineaMapper {

	public List<LineaInventario> aLineas(List<LineaRequest> lineas) {
		List<LineaInventario> resultado = new ArrayList<>();
		if (lineas == null) {
			return resultado;
		}
		for (LineaRequest l : lineas) {
			resultado.add(new LineaInventario(l.item(), null, null, l.sentido(), l.cantidad(), l.costo(), null));
		}
		return resultado;
	}

	public List<LineaResponse> aRespuestas(List<LineaInventario> lineas) {
		List<LineaResponse> resultado = new ArrayList<>();
		for (LineaInventario l : lineas) {
			resultado.add(new LineaResponse(l.item(), l.nombre(), l.unidad(), l.sentido(), l.cantidad(), l.costo(),
					l.recibida()));
		}
		return resultado;
	}

}
