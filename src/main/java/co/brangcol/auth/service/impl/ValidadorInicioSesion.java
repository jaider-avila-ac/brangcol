package co.brangcol.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.brangcol.auth.model.EstadoInicio;
import co.brangcol.shared.error.NegocioException;

@Component
public class ValidadorInicioSesion {

	public static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

	public void validar(EstadoInicio estado) {
		switch (estado) {
			case CREDENCIALES -> throw new NegocioException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS);
			case BLOQUEADA -> throw new NegocioException(HttpStatus.TOO_MANY_REQUESTS,
					"La cuenta está bloqueada temporalmente por intentos fallidos. Intente más tarde.");
			case IP_BLOQUEADA -> throw new NegocioException(HttpStatus.TOO_MANY_REQUESTS,
					"Demasiados intentos desde esta conexión. Intente más tarde.");
			case EMPRESA_NO_DISPONIBLE -> throw new NegocioException(HttpStatus.FORBIDDEN,
					"La empresa no está disponible para este usuario");
			default -> {
			}
		}
	}

	public void validarEmpresas(boolean hayEmpresas) {
		if (!hayEmpresas) {
			throw new NegocioException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS);
		}
	}

}
