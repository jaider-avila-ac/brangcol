package co.brangcol.producto.model;

import java.util.List;

public record PaginaItems(List<ItemResumen> items, long total) {
}
