package co.brangcol.inventario.dto;

import java.time.LocalDate;
import java.util.List;

public record AjusteResponse(String codigo, String numero, String clase, String bodega, LocalDate fecha, String motivo,
		String estado, List<LineaResponse> lineas) {
}
