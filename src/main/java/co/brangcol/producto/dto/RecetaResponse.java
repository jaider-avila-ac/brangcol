package co.brangcol.producto.dto;

import java.math.BigDecimal;
import java.util.List;

public record RecetaResponse(int version, boolean bloqueada, List<ComponenteDto> componentes, BigDecimal costoTotal) {
}
