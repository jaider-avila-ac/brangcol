package co.brangcol.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AceptarInvitacionRequest(
		@NotBlank(message = "El enlace no es válido") @Size(max = 64) String token,
		@Size(max = 100, message = "El nombre es demasiado largo") String nombres,
		@Size(max = 100, message = "Los apellidos son demasiado largos") String apellidos,
		@NotBlank(message = "La contraseña es obligatoria") @Size(max = 72, message = "La contraseña es demasiado larga") String contrasena) {
}
