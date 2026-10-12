package co.brangcol.producto.dto;

import java.math.BigDecimal;

public record UnidadAlternaDto(String unidad, String unidadNombre, BigDecimal factor, BigDecimal precioVenta,
		Boolean paraCompra, Boolean paraVenta) {
}
