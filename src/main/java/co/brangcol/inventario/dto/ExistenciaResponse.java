package co.brangcol.inventario.dto;

import java.math.BigDecimal;

public record ExistenciaResponse(String item, String sku, String nombre, String unidad, boolean permiteFraccion,
		BigDecimal cantidad, BigDecimal minimo, BigDecimal costoPromedio, BigDecimal valor) {
}
