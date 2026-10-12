package co.brangcol.auth.dto;

import java.util.List;

public record YoResponse(Persona persona, Empresa empresa, Rol rol, Plan plan, int minutosInactividad, String foto, String logo,
		List<String> funcionalidades, List<String> permisos) {

	public record Persona(String codigo, String correo, String nombres, String apellidos) {
	}

	public record Empresa(String codigo, String razonSocial, String nombreComercial, String estado) {
	}

	public record Rol(String nombre, String clave, boolean esPropietario) {
	}

	public record Plan(String codigo, String nombre, String estado) {
	}

}
