package co.brangcol.auth.service;

import java.util.List;

import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.ResultadoInicio;
import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;
import co.brangcol.shared.web.ClienteWeb;

public interface AuthService {

	ResultadoInicio iniciarSesion(String correo, String contrasena, String empresa, ClienteWeb cliente);

	List<EmpresaDisponible> listarEmpresas(String correo, String contrasena, ClienteWeb cliente);

	void cerrarSesion(String token);

	SesionUsuario miSesion();

	List<SesionAbierta> misSesiones();

	void cerrarSesionAbierta(String codigo);

	void cerrarOtrasSesiones();

	void cambiarContrasena(String actual, String nueva);

}
