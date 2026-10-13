package co.brangcol.producto.dto;

import java.math.BigDecimal;

public record RecetaResumenResponse(String codigo, String sku, String nombre, String tipo, String componentes,
		int cantidadComponentes, BigDecimal costo, BigDecimal precioConImpuesto) {
}
