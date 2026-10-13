package co.brangcol.inventario.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProduccionResponse(String codigo, String numero, String item, String nombre, String unidad,
		String bodega, BigDecimal cantidad, BigDecimal costoUnitario, String estado, OffsetDateTime fecha,
		String observacion) {
}
