package co.brangcol.tercero.dto;

import java.math.BigDecimal;

public record TerceroResumenResponse(String codigo, String nombre, String documento, String telefono, String correo,
		String municipio, boolean esCliente, boolean esProveedor, BigDecimal debe, BigDecimal vencido,
		BigDecimal saldoFavor, BigDecimal leDebemos, boolean activo, boolean datosCompletos) {
}
