package co.brangcol.inventario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.ProduccionRequest;
import co.brangcol.inventario.dto.ProduccionResponse;
import co.brangcol.inventario.model.NuevaProduccion;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Produccion;
import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;

@Component
public class ProduccionMapper {

	public NuevaProduccion aNueva(ProduccionRequest request) {
		return new NuevaProduccion(Textos.vacioANulo(request.item()), Textos.vacioANulo(request.bodega()),
				request.cantidad(), request.observacion());
	}

	public ProduccionResponse aRespuesta(Produccion p) {
		return new ProduccionResponse(p.codigo(), p.numero(), p.item(), p.nombre(), p.unidad(), p.bodega(),
				p.cantidad(), p.costoUnitario(), p.estado(), p.fecha(), p.observacion());
	}

	public PageResponse<ProduccionResponse> aPagina(Pagina<Produccion> pagina, int numero, int tamano) {
		List<ProduccionResponse> contenido = new ArrayList<>();
		for (Produccion p : pagina.items()) {
			contenido.add(aRespuesta(p));
		}
		return Paginas.de(contenido, pagina.total(), numero, tamano);
	}

}
