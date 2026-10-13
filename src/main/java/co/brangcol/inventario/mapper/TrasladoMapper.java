package co.brangcol.inventario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.TrasladoRequest;
import co.brangcol.inventario.dto.TrasladoResponse;
import co.brangcol.inventario.dto.TrasladoResumenResponse;
import co.brangcol.inventario.model.NuevoTraslado;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Traslado;
import co.brangcol.inventario.model.TrasladoResumen;
import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;

@Component
public class TrasladoMapper {

	private final LineaMapper lineaMapper;

	public TrasladoMapper(LineaMapper lineaMapper) {
		this.lineaMapper = lineaMapper;
	}

	public NuevoTraslado aNuevo(TrasladoRequest request) {
		return new NuevoTraslado(Textos.vacioANulo(request.origen()), Textos.vacioANulo(request.destino()),
				request.observacion(), lineaMapper.aLineas(request.lineas()));
	}

	public TrasladoResponse aRespuesta(Traslado t) {
		return new TrasladoResponse(t.codigo(), t.numero(), t.origen(), t.destino(), t.estado(), t.enviadoEn(),
				t.recibidoEn(), t.observacion(), lineaMapper.aRespuestas(t.lineas()));
	}

	public PageResponse<TrasladoResumenResponse> aPagina(Pagina<TrasladoResumen> pagina, int numero, int tamano) {
		List<TrasladoResumenResponse> contenido = new ArrayList<>();
		for (TrasladoResumen t : pagina.items()) {
			contenido.add(new TrasladoResumenResponse(t.codigo(), t.numero(), t.origen(), t.destino(), t.estado(),
					t.enviadoEn(), t.lineas()));
		}
		return Paginas.de(contenido, pagina.total(), numero, tamano);
	}

}
