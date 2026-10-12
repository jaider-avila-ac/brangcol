package co.brangcol.empresa.model;

public record Sede(String codigo, String nombre, String direccion, String municipioCodigo, String municipio,
		String telefono, boolean esPrincipal, boolean activa) {
}
