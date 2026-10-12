package co.brangcol.usuario.model;

import java.time.OffsetDateTime;

public record Usuario(String codigo, String nombres, String apellidos, String correo, String rolCodigo,
		String rolNombre, boolean esPropietario, boolean activo, OffsetDateTime ultimoIngreso, boolean esYo) {
}
