package co.brangcol.producto.model;

import java.math.BigDecimal;

public record RecetaResumen(String codigo, String sku, String nombre, String tipo, String componentes,
		int cantidadComponentes, BigDecimal costo, BigDecimal precioConImpuesto) {
}
