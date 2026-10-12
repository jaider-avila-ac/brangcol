package co.brangcol.usuario.model;

import java.time.OffsetDateTime;

public record InvitacionPendiente(String codigo, String correo, String rol, OffsetDateTime venceEn,
		OffsetDateTime creadaEn) {
}
