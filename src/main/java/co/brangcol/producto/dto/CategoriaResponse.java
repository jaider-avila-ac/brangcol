package co.brangcol.producto.dto;

public record CategoriaResponse(String codigo, String nombre, String padre, String padreNombre, boolean activa,
		long productos) {
}
