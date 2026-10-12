package co.brangcol.empresa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ConfiguracionDto(
		@Min(value = 0, message = "Decimales de cantidad entre 0 y 4") @Max(value = 4, message = "Decimales de cantidad entre 0 y 4") int decimalesCantidad,
		@Min(value = 0, message = "Decimales de precio entre 0 y 2") @Max(value = 2, message = "Decimales de precio entre 0 y 2") int decimalesPrecio,
		boolean controlaInventario,
		boolean permiteInventarioNegativo,
		@Min(value = 1, message = "Sesiones por usuario entre 1 y 50") @Max(value = 50, message = "Sesiones por usuario entre 1 y 50") int maxSesionesPorUsuario,
		@Min(value = 5, message = "Minutos de inactividad entre 5 y 1440") @Max(value = 1440, message = "Minutos de inactividad entre 5 y 1440") int minutosInactividad,
		@NotNull(message = "La forma de aplicar los pagos es obligatoria") @Pattern(regexp = "MAS_ANTIGUA|MANUAL", message = "La forma de aplicar los pagos no es válida") String aplicacionPagos) {
}
