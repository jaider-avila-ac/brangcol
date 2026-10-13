package co.brangcol.inventario.service.impl;

import org.springframework.http.HttpStatus;

import co.brangcol.shared.error.NegocioException;

// Motivo obligatorio de ajustes y anulaciones (la base exige minimo 5 letras)
final class Motivo {

	private Motivo() {
	}

	static String validar(String motivo) {
		if (motivo == null || motivo.trim().length() < 5) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Escriba el motivo (mínimo 5 letras)");
		}
		if (motivo.trim().length() > 300) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "El motivo es muy largo (máximo 300 letras)");
		}
		return motivo.trim();
	}

}
