package co.brangcol.producto.model;

import java.math.BigDecimal;
import java.util.List;

public record Item(String codigo, String sku, String nombre, String descripcion, String tipo, boolean seVende,
		boolean seCompra, boolean manejaInventario, String categoria, String categoriaNombre, String marca,
		String marcaNombre, String unidad, String unidadSimbolo, boolean permiteFraccion, String impuesto,
		BigDecimal tarifaImpuesto, BigDecimal precioVenta, BigDecimal costoPromedio, BigDecimal ultimoCosto,
		boolean activo, List<String> codigosBarras, List<UnidadAlterna> unidades) {
}
