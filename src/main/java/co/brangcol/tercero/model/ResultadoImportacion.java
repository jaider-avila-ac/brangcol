package co.brangcol.tercero.model;

import java.util.List;

public record ResultadoImportacion(int creados, int actualizados, List<ErrorFila> errores) {
}
