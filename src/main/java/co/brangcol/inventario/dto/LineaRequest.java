package co.brangcol.inventario.dto;

import java.math.BigDecimal;

public record LineaRequest(String item, String sentido, BigDecimal cantidad, BigDecimal costo) {
}
