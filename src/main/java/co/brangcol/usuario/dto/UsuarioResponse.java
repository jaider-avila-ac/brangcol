package co.brangcol.usuario.dto;

import java.time.OffsetDateTime;

public record UsuarioResponse(String codigo, String nombres, String apellidos, String correo, RolResumen rol,
		boolean esPropietario, boolean activo, OffsetDateTime ultimoIngreso, boolean esYo) {

	public record RolResumen(String codigo, String nombre) {
	}

}
