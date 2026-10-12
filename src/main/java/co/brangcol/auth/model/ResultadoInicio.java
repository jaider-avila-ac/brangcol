package co.brangcol.auth.model;

import java.time.OffsetDateTime;

public record ResultadoInicio(EstadoInicio estado, String token, OffsetDateTime expiraEn) {
}
