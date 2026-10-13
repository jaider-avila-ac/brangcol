package co.brangcol.inventario.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimientoResponse(OffsetDateTime fecha, String tipo, String tipoNombre, BigDecimal entrada,
		BigDecimal salida, BigDecimal saldo, BigDecimal costoUnitario, BigDecimal costoPromedio, String bodega,
		String documento, String observacion) {
}
