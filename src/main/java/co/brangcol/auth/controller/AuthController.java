package co.brangcol.auth.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.auth.dto.CambiarContrasenaRequest;
import co.brangcol.auth.dto.CredencialesRequest;
import co.brangcol.auth.dto.EmpresaOpcionResponse;
import co.brangcol.auth.dto.LoginRequest;
import co.brangcol.auth.dto.LoginResponse;
import co.brangcol.auth.dto.SesionResponse;
import co.brangcol.auth.dto.YoResponse;
import co.brangcol.auth.mapper.AuthMapper;
import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.EstadoInicio;
import co.brangcol.auth.model.ResultadoInicio;
import co.brangcol.auth.service.AuthService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.sesion.CookieSesion;
import co.brangcol.shared.web.ClienteWeb;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

	private final AuthService authService;

	private final AuthMapper authMapper;

	private final CookieSesion cookieSesion;

	public AuthController(AuthService authService, AuthMapper authMapper, CookieSesion cookieSesion) {
		this.authService = authService;
		this.authMapper = authMapper;
		this.cookieSesion = cookieSesion;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
		ResultadoInicio resultado = authService.iniciarSesion(request.correo(), request.contrasena(),
				request.empresa(), ClienteWeb.de(http));
		validarEstado(resultado.estado());
		if (resultado.estado() == EstadoInicio.ELEGIR_EMPRESA) {
			return ResponseEntity.ok(new LoginResponse(resultado.estado().name()));
		}
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookieSesion.crear(resultado.token(), resultado.expiraEn()).toString())
			.body(new LoginResponse(resultado.estado().name()));
	}

	@PostMapping("/empresas")
	public List<EmpresaOpcionResponse> empresas(@Valid @RequestBody CredencialesRequest request,
			HttpServletRequest http) {
		List<EmpresaDisponible> empresas = authService.listarEmpresas(request.correo(), request.contrasena(),
				ClienteWeb.de(http));
		if (empresas.isEmpty()) {
			throw new NegocioException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS);
		}
		return authMapper.aEmpresas(empresas);
	}

	@PostMapping("/salir")
	public ResponseEntity<Void> salir(@CookieValue(name = "${brangcol.sesion.cookie}", required = false) String token) {
		if (token != null && !token.isBlank()) {
			authService.cerrarSesion(token);
		}
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookieSesion.borrar().toString()).build();
	}

	@GetMapping("/yo")
	public YoResponse yo() {
		return authMapper.aYo(authService.miSesion());
	}

	@GetMapping("/sesiones")
	public List<SesionResponse> sesiones() {
		return authMapper.aSesiones(authService.misSesiones());
	}

	@DeleteMapping("/sesiones/{codigo}")
	public ResponseEntity<Void> cerrarSesion(@PathVariable String codigo) {
		authService.cerrarSesionAbierta(codigo);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/sesiones")
	public ResponseEntity<Void> cerrarOtrasSesiones() {
		authService.cerrarOtrasSesiones();
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/contrasena")
	public ResponseEntity<Void> cambiarContrasena(@Valid @RequestBody CambiarContrasenaRequest request) {
		authService.cambiarContrasena(request.actual(), request.nueva());
		return ResponseEntity.noContent().build();
	}

	private void validarEstado(EstadoInicio estado) {
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

}
