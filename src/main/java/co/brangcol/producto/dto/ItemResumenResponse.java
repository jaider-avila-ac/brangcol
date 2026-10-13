package co.brangcol.producto.dto;

import java.math.BigDecimal;

public record ItemResumenResponse(String codigo, String sku, String nombre, String tipo, String categoria,
		String unidad, BigDecimal precioVenta, BigDecimal precioConImpuesto, BigDecimal costoPromedio, boolean activo,
		BigDecimal existencia, BigDecimal minimo, String componentes, int cantidadComponentes) {
}
