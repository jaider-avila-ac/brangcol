package co.brangcol.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;

import co.brangcol.PruebaIntegracion;
import co.brangcol.shared.web.TrazaFiltro;

@AutoConfigureMetrics
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "management.server.port=0")
class ObservabilidadTest extends PruebaIntegracion {

	@LocalServerPort
	private int puerto;

	@LocalManagementPort
	private int puertoGestion;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	void saludEnUp() throws Exception {
		HttpResponse<String> respuesta = pedir(puertoGestion, "/actuator/health");
		assertEquals(200, respuesta.statusCode());
		assertTrue(respuesta.body().contains("\"UP\""));
	}

	@Test
	void metricasPrometheusConNombreDeAplicacion() throws Exception {
		pedir(puerto, "/no-existe");
		HttpResponse<String> respuesta = pedir(puertoGestion, "/actuator/prometheus");
		assertEquals(200, respuesta.statusCode());
		assertTrue(respuesta.body().contains("application=\"brangcol\""));
		assertTrue(respuesta.body().contains("http_server_requests_seconds"));
	}

	@Test
	void cadaRespuestaTraeSuTraceId() throws Exception {
		HttpResponse<String> respuesta = pedir(puerto, "/no-existe");
		assertTrue(respuesta.headers().firstValue(TrazaFiltro.CABECERA).isPresent());
	}

	@Test
	void actuatorNoQuedaEnElPuertoPublico() throws Exception {
		assertEquals(404, pedir(puerto, "/actuator/health").statusCode());
	}

	private HttpResponse<String> pedir(int puertoDestino, String ruta) throws Exception {
		HttpRequest peticion = HttpRequest.newBuilder(URI.create("http://localhost:" + puertoDestino + ruta)).build();
		return cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
	}

}
