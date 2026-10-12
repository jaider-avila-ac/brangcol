package co.brangcol.producto.dto;

import java.math.BigDecimal;
import java.util.List;

public record ItemResponse(String codigo, String sku, String nombre, String descripcion, String tipo, boolean seVende,
		boolean seCompra, boolean manejaInventario, Referencia categoria, Referencia marca, String unidad,
		String unidadSimbolo, boolean permiteFraccion, String impuesto, BigDecimal tarifaImpuesto,
		BigDecimal precioVenta, BigDecimal costoPromedio, BigDecimal ultimoCosto, boolean activo, String foto,
		List<String> codigosBarras, List<UnidadAlternaDto> unidades) {

	public record Referencia(String codigo, String nombre) {
	}

}
