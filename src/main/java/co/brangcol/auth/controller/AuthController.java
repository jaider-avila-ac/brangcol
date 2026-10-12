package co.brangcol.auth.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.auth.dto.CredencialesRequest;
import co.brangcol.auth.dto.EmpresaOpcionResponse;
import co.brangcol.auth.dto.LoginRequest;
import co.brangcol.auth.dto.LoginResponse;
import co.brangcol.auth.mapper.EmpresaOpcionMapper;
import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.EstadoInicio;
import co.brangcol.auth.model.ResultadoInicio;
import co.brangcol.auth.service.InicioSesionService;
import co.brangcol.auth.service.impl.ValidadorInicioSesion;
import co.brangcol.shared.sesion.CookieSesion;
import co.brangcol.shared.web.ClienteWeb;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final InicioSesionService inicioSesionService;

	private final ValidadorInicioSesion validador;

	private final EmpresaOpcionMapper empresaOpcionMapper;

	private final CookieSesion cookieSesion;

	public AuthController(InicioSesionService inicioSesionService, ValidadorInicioSesion validador,
			EmpresaOpcionMapper empresaOpcionMapper, CookieSesion cookieSesion) {
		this.inicioSesionService = inicioSesionService;
		this.validador = validador;
		this.empresaOpcionMapper = empresaOpcionMapper;
		this.cookieSesion = cookieSesion;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
		ResultadoInicio resultado = inicioSesionService.iniciarSesion(request.correo(), request.contrasena(),
				request.empresa(), ClienteWeb.de(http));
		validador.validar(resultado.estado());
		LoginResponse respuesta = new LoginResponse(resultado.estado().name());
		if (resultado.estado() == EstadoInicio.ELEGIR_EMPRESA) {
			return ResponseEntity.ok(respuesta);
		}
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookieSesion.crear(resultado.token(), resultado.expiraEn()).toString())
			.body(respuesta);
	}

	@PostMapping("/empresas")
	public List<EmpresaOpcionResponse> empresas(@Valid @RequestBody CredencialesRequest request,
			HttpServletRequest http) {
		List<EmpresaDisponible> empresas = inicioSesionService.listarEmpresas(request.correo(), request.contrasena(),
				ClienteWeb.de(http));
		validador.validarEmpresas(!empresas.isEmpty());
		return empresaOpcionMapper.aOpciones(empresas);
	}

	@PostMapping("/salir")
	public ResponseEntity<Void> salir(@CookieValue(name = "${brangcol.sesion.cookie}", required = false) String token) {
		if (token != null && !token.isBlank()) {
			inicioSesionService.cerrarSesion(token);
		}
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookieSesion.borrar().toString()).build();
	}

}
