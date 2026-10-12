package co.brangcol.usuario.dto;

import java.time.OffsetDateTime;

public record InvitacionPublicaResponse(String empresa, String correo, String rol, boolean tieneCuenta,
		OffsetDateTime venceEn) {
}
