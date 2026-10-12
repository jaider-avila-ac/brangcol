package co.brangcol.auth;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import co.brangcol.DatosPrueba;
import co.brangcol.PruebaIntegracion;
import jakarta.servlet.http.Cookie;

@AutoConfigureMockMvc
class AuthControllerTest extends PruebaIntegracion {

	private static final String COOKIE = "bc_sesion";

	@Autowired
	private MockMvc mvc;

	@Test
	void loginCorrectoCreaCookieSeguraYPermiteVerMisDatos() throws Exception {
		DatosPrueba.crearEmpresa("login.ok@prueba.co");
		MvcResult resultado = mvc.perform(login("login.ok@prueba.co", DatosPrueba.CLAVE, null, "10.0.0.1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.estado").value("OK"))
			.andReturn();

		String cabecera = resultado.getResponse().getHeader(HttpHeaders.SET_COOKIE);
		assertNotNull(cabecera);
		assertTrue(cabecera.contains("HttpOnly"));
		assertTrue(cabecera.contains("SameSite=Strict"));
		assertTrue(cabecera.contains("Secure"));

		mvc.perform(get("/api/v1/auth/yo").cookie(cookieDe(resultado)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.persona.correo").value("login.ok@prueba.co"))
			.andExpect(jsonPath("$.rol.clave").value("ADMINISTRADOR"))
			.andExpect(jsonPath("$.plan.codigo").value("FUNDADOR"))
			.andExpect(jsonPath("$.permisos", hasItem("ventas:factura:emitir")));
	}

	@Test
	void conLaFacturaElectronicaApagadaNoTieneEsePermiso() throws Exception {
		DatosPrueba.crearEmpresa("sin.fe@prueba.co");
		apagarFacturaElectronica("sin.fe@prueba.co");
		Cookie cookie = iniciar("sin.fe@prueba.co", "10.0.0.2");
		mvc.perform(get("/api/v1/auth/yo").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.permisos", not(hasItem("ventas:electronica:gestionar"))))
			.andExpect(jsonPath("$.funcionalidades", not(hasItem("ventas.facturacion_electronica"))));
	}

	@Test
	void contrasenaIncorrectaResponde401() throws Exception {
		DatosPrueba.crearEmpresa("clave.mala@prueba.co");
		mvc.perform(login("clave.mala@prueba.co", "OtraClave2026", null, "10.0.0.3"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos"));
	}

	@Test
	void cincoIntentosFallidosBloqueanLaCuenta() throws Exception {
		DatosPrueba.crearEmpresa("bloqueo@prueba.co");
		for (int intento = 1; intento <= 4; intento++) {
			mvc.perform(login("bloqueo@prueba.co", "OtraClave2026", null, "10.0.0.4"))
				.andExpect(status().isUnauthorized());
		}
		mvc.perform(login("bloqueo@prueba.co", "OtraClave2026", null, "10.0.0.4"))
			.andExpect(status().isTooManyRequests());
		mvc.perform(login("bloqueo@prueba.co", DatosPrueba.CLAVE, null, "10.0.0.4"))
			.andExpect(status().isTooManyRequests());
	}

	@Test
	void cambiarLaContrasenaCierraLasOtrasSesiones() throws Exception {
		DatosPrueba.crearEmpresa("cambio@prueba.co");
		Cookie primera = iniciar("cambio@prueba.co", "10.0.0.5");
		Cookie segunda = iniciar("cambio@prueba.co", "10.0.0.5");

		mvc.perform(put("/api/v1/auth/contrasena").cookie(primera)
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"actual\":\"" + DatosPrueba.CLAVE + "\",\"nueva\":\"NuevaClave2027\"}"))
			.andExpect(status().isNoContent());

		mvc.perform(get("/api/v1/auth/yo").cookie(primera)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/auth/yo").cookie(segunda)).andExpect(status().isUnauthorized());
	}

	@Test
	void contrasenaActualIncorrectaNoCierraLaSesion() throws Exception {
		DatosPrueba.crearEmpresa("cambio.malo@prueba.co");
		Cookie cookie = iniciar("cambio.malo@prueba.co", "10.0.0.6");
		mvc.perform(put("/api/v1/auth/contrasena").cookie(cookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"actual\":\"NoEsLaClave2026\",\"nueva\":\"NuevaClave2027\"}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("La contraseña actual no es correcta"));
		mvc.perform(get("/api/v1/auth/yo").cookie(cookie)).andExpect(status().isOk());
	}

	@Test
	void salirCierraLaSesionYBorraLaCookie() throws Exception {
		DatosPrueba.crearEmpresa("salir@prueba.co");
		Cookie cookie = iniciar("salir@prueba.co", "10.0.0.7");
		MvcResult resultado = mvc.perform(post("/api/v1/auth/salir").cookie(cookie))
			.andExpect(status().isNoContent())
			.andReturn();
		assertTrue(resultado.getResponse().getHeader(HttpHeaders.SET_COOKIE).contains("Max-Age=0"));
		mvc.perform(get("/api/v1/auth/yo").cookie(cookie)).andExpect(status().isUnauthorized());
	}

	@Test
	void sinCookieNoHaySesion() throws Exception {
		mvc.perform(get("/api/v1/auth/yo")).andExpect(status().isUnauthorized());
	}

	@Test
	void unaCookieVencidaNoImpideIniciarSesion() throws Exception {
		DatosPrueba.crearEmpresa("cookie.vieja@prueba.co");
		mvc.perform(login("cookie.vieja@prueba.co", DatosPrueba.CLAVE, null, "10.0.0.8")
			.cookie(new Cookie(COOKIE, "token-viejo")))
			.andExpect(status().isOk());
	}

	@Test
	void conDosEmpresasDebeElegirUna() throws Exception {
		DatosPrueba.crearEmpresa("dos.empresas@prueba.co");
		DatosPrueba.crearEmpresa("dos.empresas@prueba.co");
		mvc.perform(login("dos.empresas@prueba.co", DatosPrueba.CLAVE, null, "10.0.0.9"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.estado").value("ELEGIR_EMPRESA"));

		MvcResult empresas = mvc.perform(post("/api/v1/auth/empresas").with(desde("10.0.0.9"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"correo\":\"dos.empresas@prueba.co\",\"contrasena\":\"" + DatosPrueba.CLAVE + "\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andReturn();
		String codigo = empresas.getResponse().getContentAsString().split("\"codigo\":\"")[1].substring(0, 12);

		mvc.perform(login("dos.empresas@prueba.co", DatosPrueba.CLAVE, codigo, "10.0.0.9"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.estado").value("OK"));
	}

	@Test
	void verYCerrarMisSesiones() throws Exception {
		DatosPrueba.crearEmpresa("sesiones@prueba.co");
		Cookie actual = iniciar("sesiones@prueba.co", "10.0.0.10");
		Cookie otra = iniciar("sesiones@prueba.co", "10.0.0.10");

		mvc.perform(get("/api/v1/auth/sesiones").cookie(actual))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));

		mvc.perform(delete("/api/v1/auth/sesiones").cookie(actual)).andExpect(status().isNoContent());
		mvc.perform(get("/api/v1/auth/yo").cookie(otra)).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/v1/auth/sesiones").cookie(actual))
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].actual").value(true));
	}

	private Cookie iniciar(String correo, String ip) throws Exception {
		MvcResult resultado = mvc.perform(login(correo, DatosPrueba.CLAVE, null, ip))
			.andExpect(status().isOk())
			.andReturn();
		return cookieDe(resultado);
	}

	private Cookie cookieDe(MvcResult resultado) {
		Cookie cookie = resultado.getResponse().getCookie(COOKIE);
		assertNotNull(cookie);
		return new Cookie(COOKIE, cookie.getValue());
	}

	private MockHttpServletRequestBuilder login(String correo, String clave, String empresa, String ip) {
		String cuerpo = "{\"correo\":\"" + correo + "\",\"contrasena\":\"" + clave + "\""
				+ (empresa == null ? "" : ",\"empresa\":\"" + empresa + "\"") + "}";
		return post("/api/v1/auth/login").with(desde(ip)).contentType(MediaType.APPLICATION_JSON).content(cuerpo);
	}

	private org.springframework.test.web.servlet.request.RequestPostProcessor desde(String ip) {
		return peticion -> {
			peticion.setRemoteAddr(ip);
			return peticion;
		};
	}

	private void apagarFacturaElectronica(String correo) throws Exception {
		try (Connection con = conectar("brangcol_owner", CLAVE_DUENO); PreparedStatement ps = con.prepareStatement("""
				insert into plataforma.interruptor_modulo (funcionalidad, empresa_id, encendido, motivo)
				select 'ventas.facturacion_electronica', e.id, false, 'Prueba'
				from plataforma.empresa e where e.correo = ?
				returning id
				""")) {
			ps.setString(1, correo);
			try (ResultSet rs = ps.executeQuery()) {
				assertTrue(rs.next());
				assertEquals(1, rs.getRow());
			}
		}
	}

}
