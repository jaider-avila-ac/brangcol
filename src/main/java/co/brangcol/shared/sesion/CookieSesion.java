package co.brangcol.shared.sesion;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieSesion {

	private final String nombre;

	private final boolean segura;

	public CookieSesion(@Value("${brangcol.sesion.cookie}") String nombre,
			@Value("${brangcol.sesion.cookie-segura}") boolean segura) {
		this.nombre = nombre;
		this.segura = segura;
	}

	public String nombre() {
		return nombre;
	}

	public ResponseCookie crear(String token, OffsetDateTime expiraEn) {
		long segundos = Duration.between(OffsetDateTime.now(), expiraEn).getSeconds();
		return base(token).maxAge(Math.max(segundos, 0)).build();
	}

	public ResponseCookie borrar() {
		return base("").maxAge(0).build();
	}

	private ResponseCookie.ResponseCookieBuilder base(String valor) {
		return ResponseCookie.from(nombre, valor).httpOnly(true).secure(segura).sameSite("Strict").path("/");
	}

}
