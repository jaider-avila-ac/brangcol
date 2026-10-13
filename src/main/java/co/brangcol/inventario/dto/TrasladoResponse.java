package co.brangcol.inventario.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TrasladoResponse(String codigo, String numero, String origen, String destino, String estado,
		OffsetDateTime enviadoEn, OffsetDateTime recibidoEn, String observacion, List<LineaResponse> lineas) {
}
