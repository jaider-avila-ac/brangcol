package co.brangcol.usuario.dto;

import java.time.OffsetDateTime;

public record InvitacionResponse(String codigo, String correo, String rol, OffsetDateTime venceEn,
		OffsetDateTime creadaEn) {
}
