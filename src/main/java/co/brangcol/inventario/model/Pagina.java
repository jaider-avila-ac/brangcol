package co.brangcol.inventario.model;

import java.util.List;

public record Pagina<T>(List<T> items, long total) {
}
