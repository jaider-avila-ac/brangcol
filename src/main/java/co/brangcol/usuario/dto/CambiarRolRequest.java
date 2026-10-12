package co.brangcol.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarRolRequest(@NotBlank(message = "Elija un rol") String rol) {
}
