package co.brangcol.tercero.model;

import java.math.BigDecimal;

public record TerceroResumen(String codigo, String nombre, String tipoDocumento, String numeroDocumento, String dv,
		String telefono, String correo, String municipio, boolean esCliente, boolean esProveedor, BigDecimal debe,
		BigDecimal vencido, BigDecimal saldoFavor, BigDecimal leDebemos, boolean activo, String estadoDatos) {
}
