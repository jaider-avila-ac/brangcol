package co.brangcol.empresa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SedeRequest(
		@NotBlank(message = "El nombre es obligatorio") @Size(max = 120, message = "El nombre es demasiado largo") String nombre,
		@NotBlank(message = "La dirección es obligatoria") @Size(max = 200, message = "La dirección es demasiado larga") String direccion,
		@NotBlank(message = "El municipio es obligatorio") @Pattern(regexp = "\\d{5}", message = "El municipio no es válido") String municipio,
		@Pattern(regexp = "^$|^[0-9+ ]{7,20}$", message = "El teléfono no es válido") String telefono,
		boolean activa) {
}
