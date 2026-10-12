package co.brangcol.tercero.dto;

import java.math.BigDecimal;

public record CondicionesClienteDto(Integer plazoDias, BigDecimal cupoCredito, String controlCredito) {
}
