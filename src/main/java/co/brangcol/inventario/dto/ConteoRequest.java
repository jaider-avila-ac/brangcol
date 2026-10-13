package co.brangcol.inventario.dto;

import java.util.List;

public record ConteoRequest(String bodega, String observacion, List<LineaRequest> lineas) {
}
