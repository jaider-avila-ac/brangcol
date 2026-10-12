package co.brangcol.usuario.dto;

import java.time.OffsetDateTime;

public record InvitacionCreadaResponse(String codigo, String correo, String enlace, OffsetDateTime venceEn,
		boolean correoEnviado) {
}
