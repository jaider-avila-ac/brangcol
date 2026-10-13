package co.brangcol.inventario.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Produccion(String codigo, String numero, String item, String nombre, String unidad, String bodega,
		BigDecimal cantidad, BigDecimal costoUnitario, String estado, OffsetDateTime fecha, String observacion) {
}
