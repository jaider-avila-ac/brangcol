package co.brangcol.inventario.model;

public record FiltroExistencias(String texto, String bodega, boolean soloBajoMinimo, int pagina, int tamano) {
}
