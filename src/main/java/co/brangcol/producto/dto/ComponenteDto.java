package co.brangcol.producto.dto;

import java.math.BigDecimal;

public record ComponenteDto(String item, String nombre, String unidad, BigDecimal cantidad, BigDecimal merma,
		BigDecimal costoUnitario) {
}
