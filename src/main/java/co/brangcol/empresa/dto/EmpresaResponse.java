package co.brangcol.empresa.dto;

public record EmpresaResponse(String codigo, String tipoPersona, String razonSocial, String nombreComercial,
		String tipoDocumento, String numeroDocumento, String dv, boolean responsableIva, String direccion,
		Municipio municipio, String telefono, String correo, String estado) {

	public record Municipio(String codigo, String nombre, String departamento) {
	}

}
