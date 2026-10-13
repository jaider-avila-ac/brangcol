package co.brangcol.inventario.dto;

import java.util.List;

public record AjusteRequest(String clase, String bodega, String motivo, List<LineaRequest> lineas) {
}
