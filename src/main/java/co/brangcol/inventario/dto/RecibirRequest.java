package co.brangcol.inventario.dto;

import java.util.List;

public record RecibirRequest(List<LineaRequest> lineas) {
}
