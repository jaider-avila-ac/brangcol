package co.brangcol.inventario.model;

import java.time.LocalDate;

public record AjusteResumen(String codigo, String numero, String clase, String bodega, LocalDate fecha, String motivo,
		String estado, int lineas) {
}
