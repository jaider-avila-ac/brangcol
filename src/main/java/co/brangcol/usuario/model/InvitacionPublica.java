package co.brangcol.usuario.model;

import java.time.OffsetDateTime;

public record InvitacionPublica(String empresa, String correo, String rol, boolean tieneCuenta,
		OffsetDateTime venceEn) {
}
