package co.brangcol.inventario.model;

import java.time.LocalDate;
import java.util.List;

public record Ajuste(String codigo, String numero, String clase, String bodega, LocalDate fecha, String motivo,
		String estado, List<LineaInventario> lineas) {
}
