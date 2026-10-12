package co.brangcol.shared.correo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ResendCorreoService implements CorreoService {

	private static final Logger log = LoggerFactory.getLogger(ResendCorreoService.class);

	private final RestClient cliente;

	private final String apiKey;

	private final String remitente;

	public ResendCorreoService(@Value("${brangcol.correo.resend-api-key}") String apiKey,
			@Value("${brangcol.correo.remitente}") String remitente) {
		this.cliente = RestClient.builder().baseUrl("https://api.resend.com").build();
		this.apiKey = apiKey;
		this.remitente = remitente;
	}

	@Override
	public boolean enviar(Correo correo) {
		if (apiKey == null || apiKey.isBlank()) {
			log.info("Correo no enviado: RESEND_API_KEY no esta configurada");
			return false;
		}
		Map<String, Object> cuerpo = new HashMap<>();
		cuerpo.put("from", remitente);
		cuerpo.put("to", List.of(correo.para()));
		cuerpo.put("subject", correo.asunto());
		cuerpo.put("html", correo.html());
		try {
			cliente.post()
				.uri("/emails")
				.header("Authorization", "Bearer " + apiKey)
				.contentType(MediaType.APPLICATION_JSON)
				.body(cuerpo)
				.retrieve()
				.toBodilessEntity();
			return true;
		}
		catch (RestClientException ex) {
			log.warn("No se pudo enviar el correo: {}", ex.getMessage());
			return false;
		}
	}

}
