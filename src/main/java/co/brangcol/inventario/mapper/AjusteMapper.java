package co.brangcol.inventario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.AjusteRequest;
import co.brangcol.inventario.dto.AjusteResponse;
import co.brangcol.inventario.dto.AjusteResumenResponse;
import co.brangcol.inventario.model.Ajuste;
import co.brangcol.inventario.model.AjusteResumen;
import co.brangcol.inventario.model.NuevoAjuste;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.shared.texto.Textos;
import co.brangcol.shared.web.PageResponse;

@Component
public class AjusteMapper {

	private final LineaMapper lineaMapper;

	public AjusteMapper(LineaMapper lineaMapper) {
		this.lineaMapper = lineaMapper;
	}

	public NuevoAjuste aNuevo(AjusteRequest request) {
		return new NuevoAjuste(request.clase(), Textos.vacioANulo(request.bodega()), request.motivo(),
				lineaMapper.aLineas(request.lineas()));
	}

	public AjusteResponse aRespuesta(Ajuste a) {
		return new AjusteResponse(a.codigo(), a.numero(), a.clase(), a.bodega(), a.fecha(), a.motivo(), a.estado(),
				lineaMapper.aRespuestas(a.lineas()));
	}

	public PageResponse<AjusteResumenResponse> aPagina(Pagina<AjusteResumen> pagina, int numero, int tamano) {
		List<AjusteResumenResponse> contenido = new ArrayList<>();
		for (AjusteResumen a : pagina.items()) {
			contenido.add(new AjusteResumenResponse(a.codigo(), a.numero(), a.clase(), a.bodega(), a.fecha(),
					a.motivo(), a.estado(), a.lineas()));
		}
		return Paginas.de(contenido, pagina.total(), numero, tamano);
	}

}
