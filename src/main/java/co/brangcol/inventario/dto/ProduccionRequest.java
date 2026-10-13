package co.brangcol.inventario.dto;

import java.math.BigDecimal;

public record ProduccionRequest(String item, String bodega, BigDecimal cantidad, String observacion) {
}
