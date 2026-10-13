package co.brangcol.inventario.dto;

import java.time.LocalDate;

public record AjusteResumenResponse(String codigo, String numero, String clase, String bodega, LocalDate fecha,
		String motivo, String estado, int lineas) {
}
