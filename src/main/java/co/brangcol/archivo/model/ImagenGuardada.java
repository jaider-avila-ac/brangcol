package co.brangcol.archivo.model;

public record ImagenGuardada(String nombreOriginal, String ruta, String tipoMime, long tamano, byte[] sha256) {
}
