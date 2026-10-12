package co.brangcol.impuesto.dto;

import java.math.BigDecimal;

public record ImpuestoResponse(String codigo, String nombre, String tipo, BigDecimal tarifa) {
}
