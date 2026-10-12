package co.brangcol.empresa.model;

public record DatosEmpresa(String razonSocial, String nombreComercial, boolean responsableIva, String direccion,
		String municipioCodigo, String telefono, String correo) {
}
