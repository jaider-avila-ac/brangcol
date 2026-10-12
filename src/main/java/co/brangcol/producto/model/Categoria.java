package co.brangcol.producto.model;

public record Categoria(String codigo, String nombre, String padreCodigo, String padreNombre, boolean activa,
		long productos) {
}
