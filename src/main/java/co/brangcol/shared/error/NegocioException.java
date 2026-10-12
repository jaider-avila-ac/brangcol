package co.brangcol.shared.error;

import org.springframework.http.HttpStatus;

public class NegocioException extends RuntimeException {

	private final HttpStatus estado;

	public NegocioException(HttpStatus estado, String mensaje) {
		super(mensaje);
		this.estado = estado;
	}

	public HttpStatus getEstado() {
		return estado;
	}

}
