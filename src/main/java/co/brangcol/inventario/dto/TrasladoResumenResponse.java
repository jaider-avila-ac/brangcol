package co.brangcol.inventario.dto;

import java.time.OffsetDateTime;

public record TrasladoResumenResponse(String codigo, String numero, String origen, String destino, String estado,
		OffsetDateTime enviadoEn, int lineas) {
}
