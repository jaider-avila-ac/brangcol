package co.brangcol.shared.sesion;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SesionFiltro extends OncePerRequestFilter {

	private final String nombreCookie;

	public SesionFiltro(@Value("${brangcol.sesion.cookie}") String nombreCookie) {
		this.nombreCookie = nombreCookie;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		SesionActual.establecer(leerToken(request));
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
			if (nombreCookie.equals(cookie.getName()) && !cookie.getValue().isBlank()) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
