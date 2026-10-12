package co.brangcol.impuesto.model;

import java.math.BigDecimal;

public record Impuesto(String codigo, String nombre, String tipo, BigDecimal tarifa) {
}
