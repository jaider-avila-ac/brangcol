package co.brangcol.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearInvitacionRequest(
		@NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") @Size(max = 254) String correo,
		@NotBlank(message = "Elija un rol") String rol) {
}
