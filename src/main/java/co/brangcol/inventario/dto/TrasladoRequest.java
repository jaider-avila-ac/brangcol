package co.brangcol.inventario.dto;

import java.util.List;

public record TrasladoRequest(String origen, String destino, String observacion, List<LineaRequest> lineas) {
}
