package co.brangcol.parametro.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.parametro.dto.TipoDocumentoResponse;
import co.brangcol.parametro.model.TipoDocumento;

@Component
public class TipoDocumentoMapper {

	public List<TipoDocumentoResponse> aRespuestas(List<TipoDocumento> tipos) {
		List<TipoDocumentoResponse> respuesta = new ArrayList<>();
		for (TipoDocumento tipo : tipos) {
			respuesta.add(new TipoDocumentoResponse(tipo.codigo(), tipo.abreviatura(), tipo.nombre(), tipo.aplicaA()));
		}
		return respuesta;
	}

}
