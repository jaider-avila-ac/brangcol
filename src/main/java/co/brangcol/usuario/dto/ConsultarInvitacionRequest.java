package co.brangcol.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultarInvitacionRequest(@NotBlank(message = "El enlace no es válido") @Size(max = 64) String token) {
}
