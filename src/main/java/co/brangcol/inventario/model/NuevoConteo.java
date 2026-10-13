package co.brangcol.inventario.model;

import java.util.List;

public record NuevoConteo(String bodega, String observacion, List<LineaInventario> lineas) {
}
