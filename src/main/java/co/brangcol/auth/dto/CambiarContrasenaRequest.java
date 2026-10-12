package co.brangcol.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarContrasenaRequest(
		@NotBlank(message = "La contraseña actual es obligatoria") @Size(max = 72) String actual,
		@NotBlank(message = "La contraseña nueva es obligatoria") @Size(min = 10, max = 72, message = "La contraseña nueva debe tener entre 10 y 72 caracteres") String nueva) {
}
