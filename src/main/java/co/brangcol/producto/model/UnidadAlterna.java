package co.brangcol.producto.model;

import java.math.BigDecimal;

// 1 unidad alterna = factor unidades base (ej. 1 resma = 500 hojas)
public record UnidadAlterna(String unidad, String unidadNombre, BigDecimal factor, BigDecimal precioVenta,
		boolean paraCompra, boolean paraVenta) {
}
