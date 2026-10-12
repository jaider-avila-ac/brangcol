package co.brangcol.auth.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.ResultadoInicio;
import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;
import co.brangcol.auth.repository.AuthRepository;
import co.brangcol.auth.service.AuthService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.web.ClienteWeb;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthRepository authRepository;

	public AuthServiceImpl(AuthRepository authRepository) {
		this.authRepository = authRepository;
	}

	// Los intentos fallidos se registran en la base: este metodo nunca lanza
	// excepcion por credenciales para que la transaccion se confirme
	@Override
	@Transactional
	public ResultadoInicio iniciarSesion(String correo, String contrasena, String empresa, ClienteWeb cliente) {
		return authRepository.iniciarSesion(correo, contrasena, empresa, cliente.ip(), cliente.agente());
	}

	@Override
	@Transactional
	public List<EmpresaDisponible> listarEmpresas(String correo, String contrasena, ClienteWeb cliente) {
		return authRepository.listarEmpresas(correo, contrasena, cliente.ip(), cliente.agente());
	}

	@Override
	@Transactional
	public void cerrarSesion(String token) {
		authRepository.cerrarSesion(token);
	}

	@Override
	@Transactional(readOnly = true)
	public SesionUsuario miSesion() {
		return authRepository.miSesion();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SesionAbierta> misSesiones() {
		return authRepository.misSesiones();
	}

	@Override
	@Transactional
	public void cerrarSesionAbierta(String codigo) {
		if (authRepository.cerrarMisSesiones(codigo) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "La sesión no existe o ya fue cerrada");
		}
	}

	@Override
	@Transactional
	public void cerrarOtrasSesiones() {
		authRepository.cerrarMisSesiones(null);
	}

	@Override
	@Transactional
	public void cambiarContrasena(String actual, String nueva) {
		authRepository.cambiarMiContrasena(actual, nueva);
	}

}
