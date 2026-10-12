package co.brangcol.auth.model;

import java.time.OffsetDateTime;

public record SesionAbierta(String codigo, String empresa, String ip, String agente, OffsetDateTime iniciadaEn,
		OffsetDateTime ultimaActividad, boolean esActual) {
}
