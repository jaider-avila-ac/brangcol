package co.brangcol.inventario.model;

import java.util.List;

public record NuevoAjuste(String clase, String bodega, String motivo, List<LineaInventario> lineas) {
}
