package co.brangcol.producto.model;

import java.math.BigDecimal;

// La cantidad y el costo van en la presentacion elegida; sin presentacion, en la unidad base
public record ComponenteReceta(String item, String nombre, String unidadSimbolo, BigDecimal cantidad,
		BigDecimal merma, BigDecimal costoUnitario, String presentacion) {
}
