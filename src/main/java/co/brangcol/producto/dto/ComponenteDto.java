package co.brangcol.producto.dto;

import java.math.BigDecimal;

// presentacion: codigo de la unidad en que se escribio la cantidad (null = unidad base del insumo)
public record ComponenteDto(String item, String nombre, String unidad, BigDecimal cantidad, BigDecimal merma,
		BigDecimal costoUnitario, String presentacion) {
}
