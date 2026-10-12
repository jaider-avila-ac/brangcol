package co.brangcol.empresa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
		@NotBlank(message = "La razón social es obligatoria") @Size(max = 200, message = "La razón social es demasiado larga") String razonSocial,
		@Size(max = 200, message = "El nombre comercial es demasiado largo") String nombreComercial,
		boolean responsableIva,
		@NotBlank(message = "La dirección es obligatoria") @Size(max = 200, message = "La dirección es demasiado larga") String direccion,
		@NotBlank(message = "El municipio es obligatorio") @Pattern(regexp = "\\d{5}", message = "El municipio no es válido") String municipio,
		@Pattern(regexp = "^$|^[0-9+ ]{7,20}$", message = "El teléfono no es válido") String telefono,
		@NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") @Size(max = 254) String correo) {
}
