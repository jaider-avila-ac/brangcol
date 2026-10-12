package co.brangcol.tercero.model;

public record FiltroTerceros(String texto, RolTercero rol, boolean soloDeben, boolean incluirInactivos, int pagina,
		int tamano) {
}
