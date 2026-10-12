package co.brangcol.shared.web;

import jakarta.servlet.http.HttpServletRequest;

public record ClienteWeb(String ip, String agente) {

	public static ClienteWeb de(HttpServletRequest request) {
		return new ClienteWeb(request.getRemoteAddr(), request.getHeader("User-Agent"));
	}

}
