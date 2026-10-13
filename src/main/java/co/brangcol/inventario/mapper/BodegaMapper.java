package co.brangcol.inventario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.BodegaRequest;
import co.brangcol.inventario.dto.BodegaResponse;
import co.brangcol.inventario.model.Bodega;
import co.brangcol.inventario.model.DatosBodega;
import co.brangcol.shared.texto.Textos;

@Component
public class BodegaMapper {

	public DatosBodega aDatos(BodegaRequest request) {
		String nombre = request.nombre() == null ? null : request.nombre().trim();
		return new DatosBodega(nombre, Textos.vacioANulo(request.sede()), request.activa());
	}

	public List<BodegaResponse> aRespuestas(List<Bodega> bodegas) {
		List<BodegaResponse> respuestas = new ArrayList<>();
		for (Bodega b : bodegas) {
			respuestas.add(new BodegaResponse(b.codigo(), b.nombre(), b.sede(), b.predeterminada(), b.activa()));
		}
		return respuestas;
	}

}
