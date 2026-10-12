package co.brangcol.auth.model;

import java.util.List;

public record SesionUsuario(String personaCodigo, String correo, String nombres, String apellidos,
		String empresaCodigo, String razonSocial, String nombreComercial, String empresaEstado, String rolNombre,
		String rolClave, boolean esPropietario, String planCodigo, String planNombre, String suscripcionEstado,
		int minutosInactividad, List<String> funcionalidades, List<String> permisos) {
}
