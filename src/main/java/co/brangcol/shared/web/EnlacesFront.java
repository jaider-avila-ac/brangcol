package co.brangcol.shared.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnlacesFront {

	private final String base;

	public EnlacesFront(@Value("${brangcol.front-url}") String base) {
		this.base = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
	}

	// El token va despues de # para que el navegador no lo envie al servidor ni quede en los logs
	public String invitacion(String token) {
		return base + "/aceptar-invitacion#" + token;
	}

}
