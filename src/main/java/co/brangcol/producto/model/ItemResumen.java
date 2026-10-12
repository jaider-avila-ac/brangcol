package co.brangcol.producto.model;

import java.math.BigDecimal;

public record ItemResumen(String codigo, String sku, String nombre, String tipo, String categoria,
		String unidadSimbolo, BigDecimal precioVenta, BigDecimal precioConImpuesto, BigDecimal costoPromedio,
		boolean activo, String foto) {
}
