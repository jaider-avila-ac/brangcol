package co.brangcol.inventario;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
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
class InventarioFlujoTest extends PruebaIntegracion {

	private static int contador = 0;

	@Autowired
	private MockMvc mvc;

	private Cookie admin;

	private String principal;

	@BeforeEach
	void iniciar() throws Exception {
		contador++;
		String correo = "inventario" + contador + "@prueba.co";
		DatosPrueba.crearEmpresa(correo);
		MvcResult login = mvc.perform(json(post("/api/v1/auth/login"),
				"{\"correo\":\"" + correo + "\",\"contrasena\":\"" + DatosPrueba.CLAVE + "\"}"))
			.andReturn();
		Cookie cookie = login.getResponse().getCookie("bc_sesion");
		assertNotNull(cookie);
		admin = new Cookie("bc_sesion", cookie.getValue());
		principal = codigo(mvc.perform(get("/api/v1/inventario/bodegas").cookie(admin))
			.andExpect(jsonPath("$[0].predeterminada").value(true))
			.andReturn());
	}

	@Test
	void inicialAjusteTrasladoYConteo() throws Exception {
		String papel = producto("""
				{"tipo":"INSUMO","nombre":"Papel bond carta","unidad":"ST","precioVenta":0}
				""");
		String vinilo = producto("""
				{"tipo":"SIMPLE","nombre":"Vinilo blanco","unidad":"MTR","permiteFraccion":true,"precioVenta":18000}
				""");

		mvc.perform(json(post("/api/v1/inventario/ajustes").cookie(admin), """
				{"clase":"INICIAL","bodega":"%s","motivo":"Inventario inicial","lineas":[
				 {"item":"%s","sentido":"ENTRADA","cantidad":1000,"costo":40},
				 {"item":"%s","sentido":"ENTRADA","cantidad":30.5,"costo":9000}]}
				""".formatted(principal, papel, vinilo)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.estado").value("APLICADO"))
			.andExpect(jsonPath("$.lineas", hasSize(2)));

		mvc.perform(get("/api/v1/inventario/existencias?texto=papel").cookie(admin))
			.andExpect(jsonPath("$.content[0].cantidad").value(1000.0))
			.andExpect(jsonPath("$.content[0].valor").value(40000.0));
		mvc.perform(get("/api/v1/inventario/existencias/resumen").cookie(admin))
			.andExpect(jsonPath("$.productos").value(2))
			.andExpect(jsonPath("$.valorTotal").value(314500.0));

		mvc.perform(json(post("/api/v1/inventario/ajustes").cookie(admin), """
				{"clase":"AJUSTE","bodega":"%s","motivo":"Sale de más","lineas":[{"item":"%s","sentido":"SALIDA","cantidad":5000}]}
				""".formatted(principal, papel)))
			.andExpect(status().isConflict());
		mvc.perform(json(post("/api/v1/inventario/ajustes").cookie(admin), """
				{"clase":"AJUSTE","bodega":"%s","motivo":"x","lineas":[{"item":"%s","sentido":"SALIDA","cantidad":1}]}
				""".formatted(principal, papel)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Escriba el motivo (mínimo 5 letras)"));

		String taller = codigo(mvc.perform(json(post("/api/v1/inventario/bodegas").cookie(admin), """
				{"nombre":"Bodega taller"}
				""")).andExpect(jsonPath("$", hasSize(2))).andReturn(), 1);
		String traslado = codigo(mvc.perform(json(post("/api/v1/inventario/traslados").cookie(admin), """
				{"origen":"%s","destino":"%s","lineas":[{"item":"%s","cantidad":10}]}
				""".formatted(principal, taller, vinilo)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.estado").value("ENVIADO"))
			.andReturn());
		mvc.perform(json(post("/api/v1/inventario/traslados/" + traslado + "/recibir").cookie(admin), """
				{"lineas":[{"item":"%s","cantidad":9.5}]}
				""".formatted(vinilo)))
			.andExpect(jsonPath("$.estado").value("RECIBIDO"))
			.andExpect(jsonPath("$.lineas[0].recibida").value(9.5));
		mvc.perform(get("/api/v1/inventario/existencias?bodega=" + taller).cookie(admin))
			.andExpect(jsonPath("$.content[?(@.nombre == 'Vinilo blanco')].cantidad").value(9.5));

		mvc.perform(json(post("/api/v1/inventario/conteos").cookie(admin), """
				{"bodega":"%s","lineas":[{"item":"%s","cantidad":990}]}
				""".formatted(principal, papel)))
			.andExpect(jsonPath("$.ajuste").isNotEmpty());
		mvc.perform(json(post("/api/v1/inventario/conteos").cookie(admin), """
				{"bodega":"%s","lineas":[{"item":"%s","cantidad":990}]}
				""".formatted(principal, papel)))
			.andExpect(jsonPath("$.ajuste").value(nullValue()));

		mvc.perform(get("/api/v1/inventario/kardex/" + papel).cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].salida").value(10.0))
			.andExpect(jsonPath("$.content[0].saldo").value(990.0))
			.andExpect(jsonPath("$.content[1].entrada").value(1000.0));
		mvc.perform(get("/api/v1/inventario/ajustes").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(2)));
		mvc.perform(get("/api/v1/inventario/traslados").cookie(admin))
			.andExpect(jsonPath("$.content[0].estado").value("RECIBIDO"));

		mvc.perform(json(put("/api/v1/inventario/existencias/minimo").cookie(admin), """
				{"bodega":"%s","item":"%s","minimo":1000}
				""".formatted(principal, papel)))
			.andExpect(status().isNoContent());
		mvc.perform(get("/api/v1/inventario/existencias?soloBajoMinimo=true").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].nombre").value("Papel bond carta"));
		mvc.perform(get("/api/v1/productos?soloBajoMinimo=true").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].existencia").value(990.0))
			.andExpect(jsonPath("$.content[0].minimo").value(1000.0));
	}

	@Test
	void anularUnAjusteDevuelveLasExistencias() throws Exception {
		String papel = producto("""
				{"tipo":"INSUMO","nombre":"Papel","unidad":"ST","precioVenta":0}
				""");
		String ajuste = codigo(mvc.perform(json(post("/api/v1/inventario/ajustes").cookie(admin), """
				{"clase":"INICIAL","bodega":"%s","motivo":"Inventario inicial","lineas":[{"item":"%s","sentido":"ENTRADA","cantidad":50,"costo":10}]}
				""".formatted(principal, papel))).andReturn());
		mvc.perform(json(post("/api/v1/inventario/ajustes/" + ajuste + "/anular").cookie(admin), """
				{"motivo":"Se registró dos veces"}
				"""))
			.andExpect(jsonPath("$.estado").value("ANULADO"));
		mvc.perform(get("/api/v1/inventario/existencias?texto=papel").cookie(admin))
			.andExpect(jsonPath("$.content[0].cantidad").value(0.0));
	}

	@Test
	void producirUnFabricadoGastaSusInsumos() throws Exception {
		String papel = producto("""
				{"tipo":"INSUMO","nombre":"Papel bond carta","unidad":"ST","precioVenta":0}
				""");
		String sobre = producto("""
				{"tipo":"FABRICADO","nombre":"Sobre de diezmos","unidad":"94","precioVenta":350}
				""");
		mvc.perform(json(put("/api/v1/productos/" + sobre + "/receta").cookie(admin),
				"{\"componentes\":[{\"item\":\"" + papel + "\",\"cantidad\":1}]}"))
			.andExpect(status().isOk());
		mvc.perform(json(post("/api/v1/inventario/ajustes").cookie(admin), """
				{"clase":"INICIAL","bodega":"%s","motivo":"Inventario inicial","lineas":[{"item":"%s","sentido":"ENTRADA","cantidad":500,"costo":40}]}
				""".formatted(principal, papel)))
			.andExpect(status().isCreated());

		String orden = codigo(mvc.perform(json(post("/api/v1/inventario/producciones").cookie(admin), """
				{"item":"%s","bodega":"%s","cantidad":100}
				""".formatted(sobre, principal)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.estado").value("EJECUTADA"))
			.andExpect(jsonPath("$.costoUnitario").value(40.0))
			.andReturn());
		mvc.perform(get("/api/v1/inventario/existencias?texto=papel").cookie(admin))
			.andExpect(jsonPath("$.content[0].cantidad").value(400.0));
		mvc.perform(get("/api/v1/inventario/kardex/" + papel).cookie(admin))
			.andExpect(jsonPath("$.content[0].documento").value(org.hamcrest.Matchers.startsWith("Producción OE")));
		mvc.perform(json(post("/api/v1/inventario/producciones").cookie(admin), """
				{"item":"%s","bodega":"%s","cantidad":1000}
				""".formatted(sobre, principal)))
			.andExpect(status().isConflict());

		mvc.perform(json(post("/api/v1/inventario/producciones/" + orden + "/anular").cookie(admin), """
				{"motivo":"Se registró por error"}
				"""))
			.andExpect(jsonPath("$.estado").value("ANULADA"));
		mvc.perform(get("/api/v1/inventario/existencias?texto=papel").cookie(admin))
			.andExpect(jsonPath("$.content[0].cantidad").value(500.0));
	}

	@Test
	void unaRecetaGastaPorResmasYOtraPorHojas() throws Exception {
		String papel = producto("""
				{"tipo":"INSUMO","nombre":"Papel bond carta","unidad":"ST","precioVenta":0,
				 "unidades":[{"unidad":"RM","factor":500,"paraCompra":true,"paraVenta":false}]}
				""");
		String agenda = producto("""
				{"tipo":"FABRICADO","nombre":"Agenda","unidad":"94","precioVenta":25000}
				""");
		mvc.perform(json(put("/api/v1/productos/" + agenda + "/receta").cookie(admin),
				"{\"componentes\":[{\"item\":\"" + papel + "\",\"cantidad\":1,\"presentacion\":\"RM\"}]}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.componentes[0].cantidad").value(1.0))
			.andExpect(jsonPath("$.componentes[0].unidad").value("resma"))
			.andExpect(jsonPath("$.componentes[0].presentacion").value("RM"));
		mvc.perform(get("/api/v1/productos?tipo=FABRICADO").cookie(admin))
			.andExpect(jsonPath("$.content[0].componentes").value("Papel bond carta 1 resma"));

		mvc.perform(json(post("/api/v1/inventario/ajustes").cookie(admin), """
				{"clase":"INICIAL","bodega":"%s","motivo":"Inventario inicial","lineas":[{"item":"%s","sentido":"ENTRADA","cantidad":1500,"costo":40}]}
				""".formatted(principal, papel)))
			.andExpect(status().isCreated());
		mvc.perform(json(post("/api/v1/inventario/producciones").cookie(admin), """
				{"item":"%s","bodega":"%s","cantidad":2}
				""".formatted(agenda, principal)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.costoUnitario").value(20000.0));
		mvc.perform(get("/api/v1/inventario/existencias?texto=papel").cookie(admin))
			.andExpect(jsonPath("$.content[0].cantidad").value(500.0));
	}

	private String producto(String cuerpo) throws Exception {
		return codigo(mvc.perform(json(post("/api/v1/productos").cookie(admin), cuerpo))
			.andExpect(status().isCreated())
			.andReturn());
	}

	private String codigo(MvcResult resultado) throws Exception {
		return codigo(resultado, 0);
	}

	private String codigo(MvcResult resultado, int posicion) throws Exception {
		return resultado.getResponse().getContentAsString().split("\"codigo\":\"")[posicion + 1].substring(0, 12);
	}

	private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder peticion, String cuerpo) {
		return peticion.contentType(MediaType.APPLICATION_JSON).content(cuerpo);
	}

}
