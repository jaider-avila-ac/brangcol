package co.brangcol.tercero.model;

public record DatosTercero(String tipoPersona, String tipoDocumento, String numeroDocumento, String razonSocial,
		String nombres, String apellidos, String nombreComercial, boolean responsableIva, String direccion,
		String municipio, String correo, String telefono, String notas, CondicionesCliente cliente,
		CondicionesProveedor proveedor) {
}
