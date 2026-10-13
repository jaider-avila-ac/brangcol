package co.brangcol.inventario.dto;

import java.math.BigDecimal;

public record MinimoRequest(String bodega, String item, BigDecimal minimo) {
}
