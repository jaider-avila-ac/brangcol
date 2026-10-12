package co.brangcol.tercero.dto;

import java.math.BigDecimal;

public record TerceroResponse(String codigo, String tipoPersona, String tipoDocumento, String numeroDocumento,
		String dv, String razonSocial, String nombres, String apellidos, String nombreComercial, String nombre,
		boolean responsableIva, String direccion, Municipio municipio, String correo, String telefono, String notas,
		boolean activo, boolean datosCompletos, CondicionesClienteDto cliente, CondicionesProveedorDto proveedor,
		BigDecimal debe, BigDecimal saldoFavor, BigDecimal leDebemos) {

	public record Municipio(String codigo, String nombre) {
	}

}
