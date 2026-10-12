package co.brangcol.shared.sesion;

import java.io.IOException;
import java.util.Set;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SesionFiltro extends OncePerRequestFilter {

	// En estas rutas una cookie vieja o vencida no debe impedir la operacion
	private static final Set<String> RUTAS_SIN_SESION = Set.of("/api/v1/auth/login", "/api/v1/auth/empresas",
			"/api/v1/auth/salir", "/api/v1/invitaciones/consultar", "/api/v1/invitaciones/aceptar");

	private final CookieSesion cookieSesion;

	public SesionFiltro(CookieSesion cookieSesion) {
		this.cookieSesion = cookieSesion;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if (!RUTAS_SIN_SESION.contains(request.getRequestURI())) {
			SesionActual.establecer(leerToken(request));
		}
		try {
			chain.doFilter(request, response);
		}
		finally {
			SesionActual.limpiar();
			MDC.remove(SesionTransactionManager.MDC_EMPRESA);
		}
	}

	private String leerToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookieSesion.nombre().equals(cookie.getName()) && !cookie.getValue().isBlank()) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
