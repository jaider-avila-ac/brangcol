package co.brangcol.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
		@NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") @Size(max = 254) String correo,
		@NotBlank(message = "La contraseña es obligatoria") @Size(max = 72, message = "La contraseña es demasiado larga") String contrasena,
		@Size(max = 12) String empresa) {
}
