package co.brangcol.auth.dto;

import java.time.OffsetDateTime;

public record SesionResponse(String codigo, String empresa, String ip, String dispositivo, OffsetDateTime iniciadaEn,
		OffsetDateTime ultimaActividad, boolean actual) {
}
