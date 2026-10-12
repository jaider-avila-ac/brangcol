package co.brangcol.tercero.dto;

import jakarta.validation.constraints.NotBlank;

public record TerceroRequest(@NotBlank(message = "Indique si es persona natural o empresa") String tipoPersona,
		String tipoDocumento, String numeroDocumento, String razonSocial, String nombres, String apellidos,
		String nombreComercial, Boolean responsableIva, String direccion, String municipio, String correo,
		String telefono, String notas, CondicionesClienteDto cliente, CondicionesProveedorDto proveedor) {
}
