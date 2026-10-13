package co.brangcol.producto.model;

public record FiltroItems(String texto, String tipo, String categoria, boolean soloComponentes, boolean soloBajoMinimo,
		boolean incluirInactivos, int pagina, int tamano) {
}
