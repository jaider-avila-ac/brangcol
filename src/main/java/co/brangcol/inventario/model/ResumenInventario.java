package co.brangcol.inventario.model;

import java.math.BigDecimal;

public record ResumenInventario(long productos, BigDecimal valorTotal, long bajoMinimo) {
}
