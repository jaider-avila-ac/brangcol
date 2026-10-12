package co.brangcol.producto.model;

import java.math.BigDecimal;
import java.util.List;

public record DatosItem(String tipo, String sku, String nombre, String descripcion, String categoria, String marca,
		String unidad, boolean permiteFraccion, String impuesto, BigDecimal precioVenta, boolean seVende,
		boolean seCompra, List<String> codigosBarras, List<UnidadAlterna> unidades) {
}
