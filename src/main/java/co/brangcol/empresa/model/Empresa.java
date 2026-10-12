package co.brangcol.empresa.model;

public record Empresa(String codigo, String tipoPersona, String razonSocial, String nombreComercial,
		String tipoDocumento, String numeroDocumento, String dv, boolean responsableIva, String direccion,
		String municipioCodigo, String municipio, String departamento, String telefono, String correo,
		String estado) {
}
