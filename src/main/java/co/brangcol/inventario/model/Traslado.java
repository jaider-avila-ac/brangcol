package co.brangcol.inventario.model;

import java.time.OffsetDateTime;
import java.util.List;

public record Traslado(String codigo, String numero, String origen, String destino, String estado,
		OffsetDateTime enviadoEn, OffsetDateTime recibidoEn, String observacion, List<LineaInventario> lineas) {
}
