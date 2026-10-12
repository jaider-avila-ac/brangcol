package co.brangcol.producto.model;

import java.math.BigDecimal;

public record ComponenteReceta(String item, String nombre, String unidadSimbolo, BigDecimal cantidad,
		BigDecimal merma, BigDecimal costoUnitario) {
}
