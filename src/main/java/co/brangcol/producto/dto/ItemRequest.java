package co.brangcol.producto.dto;

import java.math.BigDecimal;
import java.util.List;

public record ItemRequest(String tipo, String sku, String nombre, String descripcion, String categoria, String marca,
		String unidad, Boolean permiteFraccion, String impuesto, BigDecimal precioVenta, Boolean seVende,
		Boolean seCompra, List<String> codigosBarras, List<UnidadAlternaDto> unidades) {
}
