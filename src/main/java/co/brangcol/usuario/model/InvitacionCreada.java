package co.brangcol.usuario.model;

import java.time.OffsetDateTime;

public record InvitacionCreada(String codigo, String correo, String token, OffsetDateTime venceEn, String empresa,
		String rol, String invitadoPor) {
}
