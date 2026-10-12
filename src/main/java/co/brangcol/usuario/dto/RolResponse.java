package co.brangcol.usuario.dto;

import java.util.List;

public record RolResponse(String codigo, String nombre, String descripcion, String clave, List<Permiso> permisos) {

	public record Permiso(String codigo, String descripcion) {
	}

}
