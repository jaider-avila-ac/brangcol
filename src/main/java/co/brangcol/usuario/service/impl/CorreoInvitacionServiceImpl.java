package co.brangcol.usuario.service.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import co.brangcol.shared.correo.Correo;
import co.brangcol.shared.correo.CorreoService;
import co.brangcol.usuario.model.InvitacionCreada;
import co.brangcol.usuario.service.CorreoInvitacionService;

@Service
public class CorreoInvitacionServiceImpl implements CorreoInvitacionService {

	private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy",
			Locale.forLanguageTag("es-CO"));

	private static final String PLANTILLA = """
			<div style="font-family:Arial,sans-serif;max-width:520px;margin:0 auto;color:#1f2933">
			  <div style="background:#1f3a5f;color:#ffffff;padding:18px 24px;font-size:20px;font-weight:bold">BrangCol</div>
			  <div style="border:1px solid #d9dde3;border-top:none;padding:24px">
			    <p>%s le invita a usar BrangCol en <strong>%s</strong> con el rol <strong>%s</strong>.</p>
			    <p><a href="%s" style="display:inline-block;background:#1f3a5f;color:#ffffff;padding:10px 18px;text-decoration:none">Aceptar invitación</a></p>
			    <p style="color:#6b7280;font-size:13px">El enlace vence el %s. Si no esperaba esta invitación, ignore este correo.</p>
			  </div>
			</div>
			""";

	private final CorreoService correoService;

	public CorreoInvitacionServiceImpl(CorreoService correoService) {
		this.correoService = correoService;
	}

	@Override
	public boolean enviar(InvitacionCreada invitacion, String enlace) {
		String vence = FECHA.format(invitacion.venceEn().atZoneSameInstant(ZoneId.of("America/Bogota")));
		String html = PLANTILLA.formatted(HtmlUtils.htmlEscape(invitacion.invitadoPor()),
				HtmlUtils.htmlEscape(invitacion.empresa()), HtmlUtils.htmlEscape(invitacion.rol()),
				HtmlUtils.htmlEscape(enlace), vence);
		return correoService.enviar(new Correo(invitacion.correo(), "Invitación a " + invitacion.empresa(), html));
	}

}
