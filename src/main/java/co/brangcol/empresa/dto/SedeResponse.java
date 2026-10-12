package co.brangcol.empresa.dto;

public record SedeResponse(String codigo, String nombre, String direccion, EmpresaResponse.Municipio municipio,
		String telefono, boolean esPrincipal, boolean activa) {
}
