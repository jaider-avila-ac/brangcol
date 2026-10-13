package co.brangcol.inventario.dto;

import java.math.BigDecimal;

public record ResumenInventarioResponse(long productos, BigDecimal valorTotal, long bajoMinimo) {
}
