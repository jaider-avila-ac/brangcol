package co.brangcol.tercero.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.tercero.dto.ResultadoImportacionResponse;
import co.brangcol.tercero.model.ErrorFila;
import co.brangcol.tercero.model.ResultadoImportacion;

@Component
public class ImportacionMapper {

	public ResultadoImportacionResponse aRespuesta(ResultadoImportacion resultado) {
		List<ResultadoImportacionResponse.Error> errores = new ArrayList<>();
		for (ErrorFila error : resultado.errores()) {
			errores.add(new ResultadoImportacionResponse.Error(error.fila(), error.mensaje()));
		}
		return new ResultadoImportacionResponse(resultado.creados(), resultado.actualizados(), errores);
	}

}
