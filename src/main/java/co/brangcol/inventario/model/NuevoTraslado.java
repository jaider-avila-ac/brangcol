package co.brangcol.inventario.model;

import java.util.List;

public record NuevoTraslado(String origen, String destino, String observacion, List<LineaInventario> lineas) {
}
