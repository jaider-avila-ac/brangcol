package co.brangcol.shared.error;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

final class EstadoPorCodigo {

	private static final Map<String, HttpStatus> ESTADOS = new HashMap<>();

	static {
		registrar(HttpStatus.UNAUTHORIZED, "BC001", "BC113");
		registrar(HttpStatus.FORBIDDEN, "BC090", "BC091", "BC114", "BC115");
		registrar(HttpStatus.NOT_FOUND, "BC002", "BC100");
		registrar(HttpStatus.TOO_MANY_REQUESTS, "BC110", "BC112");
		registrar(HttpStatus.BAD_REQUEST, "BC003", "BC111", "BC116");
		registrar(HttpStatus.CONFLICT, "BC010", "BC011", "BC020", "BC021", "BC030", "BC040", "BC050", "BC061",
				"BC070", "BC071", "BC072", "BC080");
	}

	private EstadoPorCodigo() {
	}

	private static void registrar(HttpStatus estado, String... codigos) {
		for (String codigo : codigos) {
			ESTADOS.put(codigo, estado);
		}
	}

	static HttpStatus de(String codigo) {
		HttpStatus estado = ESTADOS.get(codigo);
		if (estado == null) {
			return HttpStatus.UNPROCESSABLE_CONTENT;
		}
		return estado;
	}

}
