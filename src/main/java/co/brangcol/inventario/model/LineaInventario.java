package co.brangcol.inventario.model;

import java.math.BigDecimal;

public record LineaInventario(String item, String nombre, String unidad, String sentido, BigDecimal cantidad,
		BigDecimal costo, BigDecimal recibida) {
}
