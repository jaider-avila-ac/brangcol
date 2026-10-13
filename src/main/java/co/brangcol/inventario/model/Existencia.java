package co.brangcol.inventario.model;

import java.math.BigDecimal;

public record Existencia(String item, String sku, String nombre, String unidad, boolean permiteFraccion,
		BigDecimal cantidad, BigDecimal minimo, BigDecimal costoPromedio) {
}
