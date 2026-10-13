package co.brangcol.inventario.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Movimiento(OffsetDateTime fecha, String tipo, String tipoNombre, int signo, BigDecimal cantidad,
		BigDecimal saldo, BigDecimal costoUnitario, BigDecimal costoPromedio, String bodega, String documentoTipo,
		String documentoNumero, String observacion) {
}
