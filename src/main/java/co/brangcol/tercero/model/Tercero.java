package co.brangcol.tercero.model;

import java.math.BigDecimal;

public record Tercero(String codigo, String tipoPersona, String tipoDocumento, String numeroDocumento, String dv,
		String razonSocial, String nombres, String apellidos, String nombreComercial, String nombreCompleto,
		boolean responsableIva, String direccion, String municipioCodigo, String municipioNombre, String correo,
		String telefono, String notas, boolean activo, String estadoDatos, CondicionesCliente cliente,
		CondicionesProveedor proveedor, BigDecimal debe, BigDecimal saldoFavor, BigDecimal leDebemos) {
}
