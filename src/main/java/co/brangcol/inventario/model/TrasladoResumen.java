package co.brangcol.inventario.model;

import java.time.OffsetDateTime;

public record TrasladoResumen(String codigo, String numero, String origen, String destino, String estado,
		OffsetDateTime enviadoEn, int lineas) {
}
