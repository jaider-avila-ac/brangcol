package co.brangcol.usuario;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import co.brangcol.DatosPrueba;
import co.brangcol.PruebaIntegracion;
import jakarta.servlet.http.Cookie;

@AutoConfigureMockMvc
class UsuariosFlujoTest extends PruebaIntegracion {

	@Autowired
	private MockMvc mvc;

	@Test
	void elAdministradorInvitaUnaCajeraQueEntraComoVendedora() throws Exception {
		DatosPrueba.crearEmpresa("admin.flujo@prueba.co");
		Cookie admin = iniciar("admin.flujo@prueba.co", DatosPrueba.CLAVE);

		String rolVendedor = valor(mvc.perform(get("/api/v1/roles").cookie(admin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[?(@.clave == 'VENDEDOR')].permisos[*].codigo", hasItem("ventas:pos:vender")))
			.andReturn(), "VENDEDOR");

		MvcResult invitacion = mvc.perform(json(post("/api/v1/usuarios/invitaciones").cookie(admin),
				"{\"correo\":\"Cajera.Flujo@prueba.co\",\"rol\":\"" + rolVendedor + "\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.correo").value("cajera.flujo@prueba.co"))
			.andExpect(jsonPath("$.enlace", startsWith("http://localhost:5173/aceptar-invitacion#")))
			.andExpect(jsonPath("$.correoEnviado").value(false))
			.andReturn();
		String enlace = invitacion.getResponse().getContentAsString().split("\"enlace\":\"")[1].split("\"")[0];
		String token = enlace.substring(enlace.indexOf('#') + 1);

		mvc.perform(get("/api/v1/usuarios/invitaciones").cookie(admin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)));

		mvc.perform(json(post("/api/v1/invitaciones/consultar"), "{\"token\":\"" + token + "\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.rol").value("Vendedor / Cajero"))
			.andExpect(jsonPath("$.tieneCuenta").value(false));

		mvc.perform(json(post("/api/v1/invitaciones/aceptar"),
				"{\"token\":\"" + token + "\",\"nombres\":\"Cajera\",\"apellidos\":\"Flujo\",\"contrasena\":\"CajeraFlujo2026\"}"))
			.andExpect(status().isNoContent());

		mvc.perform(json(post("/api/v1/invitaciones/consultar"), "{\"token\":\"" + token + "\"}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("La invitación no es válida, ya venció o ya fue usada"));

		Cookie cajera = iniciar("cajera.flujo@prueba.co", "CajeraFlujo2026");
		mvc.perform(get("/api/v1/auth/yo").cookie(cajera))
			.andExpect(jsonPath("$.rol.clave").value("VENDEDOR"))
			.andExpect(jsonPath("$.permisos", not(hasItem("plataforma:usuario:consultar"))));
		mvc.perform(get("/api/v1/usuarios").cookie(cajera)).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/empresa").cookie(cajera)).andExpect(status().isForbidden());

		String cajeraCodigo = valor(mvc.perform(get("/api/v1/usuarios").cookie(admin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andReturn(), "cajera.flujo@prueba.co");
		mvc.perform(json(put("/api/v1/usuarios/" + cajeraCodigo + "/estado").cookie(admin), "{\"activo\":false}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.activo").value(false));
		mvc.perform(get("/api/v1/auth/yo").cookie(cajera)).andExpect(status().isUnauthorized());
	}

	@Test
	void elAdministradorNoPuedeDesactivarseNiCambiarSuRol() throws Exception {
		DatosPrueba.crearEmpresa("admin.propio@prueba.co");
		Cookie admin = iniciar("admin.propio@prueba.co", DatosPrueba.CLAVE);
		String yo = valor(mvc.perform(get("/api/v1/usuarios").cookie(admin)).andReturn(), "admin.propio@prueba.co");
		mvc.perform(json(put("/api/v1/usuarios/" + yo + "/estado").cookie(admin), "{\"activo\":false}"))
			.andExpect(status().isConflict());
	}

	@Test
	void datosConfiguracionYSedesDeLaEmpresa() throws Exception {
		DatosPrueba.crearEmpresa("admin.empresa@prueba.co");
		Cookie admin = iniciar("admin.empresa@prueba.co", DatosPrueba.CLAVE);

		mvc.perform(get("/api/v1/parametros/municipios?buscar=barranq").cookie(admin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].codigo").value("08001"));

		mvc.perform(json(put("/api/v1/empresa").cookie(admin), """
				{"razonSocial":"Empresa Editada SAS","nombreComercial":"Editada","responsableIva":true,
				 "direccion":"Calle 50 # 10-20","municipio":"08001","telefono":"3001112233","correo":"Contacto@Editada.co"}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.razonSocial").value("Empresa Editada SAS"))
			.andExpect(jsonPath("$.correo").value("contacto@editada.co"))
			.andExpect(jsonPath("$.municipio.nombre").value("Barranquilla"));

		mvc.perform(json(put("/api/v1/empresa").cookie(admin), """
				{"razonSocial":"X","responsableIva":true,"direccion":"Calle 1","municipio":"99999","correo":"a@b.co"}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("El municipio no es válido"));

		mvc.perform(json(put("/api/v1/empresa/configuracion").cookie(admin), """
				{"decimalesCantidad":3,"decimalesPrecio":0,"controlaInventario":true,"permiteInventarioNegativo":false,
				 "maxSesionesPorUsuario":3,"minutosInactividad":60,"aplicacionPagos":"MAS_ANTIGUA"}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.decimalesCantidad").value(3));

		mvc.perform(json(post("/api/v1/empresa/sedes").cookie(admin), """
				{"nombre":"Sede Norte","direccion":"Calle 100","municipio":"08001","telefono":"","activa":true}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nombre").value("Sede Norte"));
		mvc.perform(get("/api/v1/empresa/sedes").cookie(admin))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].esPrincipal").value(true));
	}

	@Test
	void unaInvitacionRevocadaYaNoSirve() throws Exception {
		DatosPrueba.crearEmpresa("admin.revoca@prueba.co");
		Cookie admin = iniciar("admin.revoca@prueba.co", DatosPrueba.CLAVE);
		String rol = valor(mvc.perform(get("/api/v1/roles").cookie(admin)).andReturn(), "CONSULTA");
		MvcResult creada = mvc.perform(json(post("/api/v1/usuarios/invitaciones").cookie(admin),
				"{\"correo\":\"revocada@prueba.co\",\"rol\":\"" + rol + "\"}"))
			.andExpect(status().isCreated())
			.andReturn();
		String codigo = creada.getResponse().getContentAsString().split("\"codigo\":\"")[1].substring(0, 12);
		mvc.perform(delete("/api/v1/usuarios/invitaciones/" + codigo).cookie(admin)).andExpect(status().isNoContent());
		mvc.perform(get("/api/v1/usuarios/invitaciones").cookie(admin)).andExpect(jsonPath("$", hasSize(0)));
	}

	private Cookie iniciar(String correo, String clave) throws Exception {
		MvcResult resultado = mvc.perform(json(post("/api/v1/auth/login"),
				"{\"correo\":\"" + correo + "\",\"contrasena\":\"" + clave + "\"}"))
			.andExpect(status().isOk())
			.andReturn();
		Cookie cookie = resultado.getResponse().getCookie("bc_sesion");
		assertNotNull(cookie);
		return new Cookie("bc_sesion", cookie.getValue());
	}

	private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder peticion, String cuerpo) {
		return peticion.contentType(MediaType.APPLICATION_JSON).content(cuerpo);
	}

	// Devuelve el "codigo" del objeto del arreglo JSON que contiene el texto indicado
	private String valor(MvcResult resultado, String texto) throws Exception {
		String cuerpo = resultado.getResponse().getContentAsString();
		String[] objetos = cuerpo.split("\\{\"codigo\":\"");
		for (String objeto : objetos) {
			if (objeto.contains(texto)) {
				return objeto.substring(0, 12);
			}
		}
		throw new AssertionError("No se encontro " + texto + " en " + cuerpo);
	}

}
