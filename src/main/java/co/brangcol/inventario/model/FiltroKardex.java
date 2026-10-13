package co.brangcol.inventario.model;

import java.time.LocalDate;

public record FiltroKardex(String item, String bodega, LocalDate desde, LocalDate hasta, int pagina, int tamano) {
}
