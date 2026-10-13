package co.brangcol.inventario.dto;

import java.math.BigDecimal;

public record LineaResponse(String item, String nombre, String unidad, String sentido, BigDecimal cantidad,
		BigDecimal costo, BigDecimal recibida) {
}
