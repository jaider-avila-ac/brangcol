package co.brangcol.tercero.dto;

import java.util.List;

public record ResultadoImportacionResponse(int creados, int actualizados, List<Error> errores) {

	public record Error(int fila, String mensaje) {
	}

}
