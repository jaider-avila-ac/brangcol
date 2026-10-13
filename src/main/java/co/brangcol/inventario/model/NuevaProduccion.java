package co.brangcol.inventario.model;

import java.math.BigDecimal;

public record NuevaProduccion(String item, String bodega, BigDecimal cantidad, String observacion) {
}
