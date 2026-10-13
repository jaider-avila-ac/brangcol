package co.brangcol.producto;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import co.brangcol.DatosPrueba;
import co.brangcol.PruebaIntegracion;
import jakarta.servlet.http.Cookie;

@AutoConfigureMockMvc
class ProductosFlujoTest extends PruebaIntegracion {

	private static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10, 0, 0, 0, 13, 'I', 'H', 'D', 'R' };

	private static int contador = 0;

	@Autowired
	private MockMvc mvc;

	private Cookie admin;

	private String iva19;

	@BeforeEach
	void iniciar() throws Exception {
		contador++;
		String correo = "productos" + contador + "@prueba.co";
		DatosPrueba.crearEmpresa(correo);
		MvcResult login = mvc.perform(json(post("/api/v1/auth/login"),
				"{\"correo\":\"" + correo + "\",\"contrasena\":\"" + DatosPrueba.CLAVE + "\"}"))
			.andReturn();
		Cookie cookie = login.getResponse().getCookie("bc_sesion");
		assertNotNull(cookie);
		admin = new Cookie("bc_sesion", cookie.getValue());
		String impuestos = mvc.perform(get("/api/v1/impuestos").cookie(admin)).andReturn().getResponse()
			.getContentAsString();
		iva19 = impuestos.split("\"codigo\":\"")[1].substring(0, 12);
	}

	@Test
	void productoSimpleConCodigoDeBarrasYCodigoInternoAutomatico() throws Exception {
		mvc.perform(get("/api/v1/parametros/unidades").cookie(admin))
			.andExpect(jsonPath("$[*].codigo", hasItem("ST")))
			.andExpect(jsonPath("$[*].codigo", hasItem("RM")));

		mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"SIMPLE","nombre":"Camiseta básica","unidad":"94","impuesto":"%s","precioVenta":10084.03,
				 "codigosBarras":["7701234567890"]}
				""".formatted(iva19)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.sku").value("P00001"))
			.andExpect(jsonPath("$.manejaInventario").value(true))
			.andExpect(jsonPath("$.codigosBarras[0]").value("7701234567890"));

		mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"SIMPLE","nombre":"Otra","unidad":"94","precioVenta":1000,"codigosBarras":["7701234567890"]}
				"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("El código de barras 7701234567890 ya lo usa Camiseta básica"));

		mvc.perform(get("/api/v1/productos?texto=camisa").cookie(admin))
			.andExpect(jsonPath("$.content[0].nombre").value("Camiseta básica"))
			.andExpect(jsonPath("$.content[0].precioConImpuesto").value(12000.0));
		mvc.perform(get("/api/v1/productos?texto=7701234567890").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)));
	}

	@Test
	void servicioQueDescuentaSusInsumos() throws Exception {
		String papel = codigo(mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"INSUMO","nombre":"Papel bond carta","unidad":"ST","precioVenta":0,
				 "unidades":[{"unidad":"RM","factor":500,"paraCompra":true,"paraVenta":false}]}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.seVende").value(false))
			.andExpect(jsonPath("$.unidades[0].factor").value(500))
			.andReturn());
		String goma = codigo(mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"INSUMO","nombre":"Goma líquida","unidad":"MLT","permiteFraccion":true,"precioVenta":0}
				""")).andExpect(status().isCreated()).andReturn());
		String sobre = codigo(mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"SERVICIO","nombre":"Sobre de diezmos","unidad":"94","impuesto":"%s","precioVenta":1500}
				""".formatted(iva19)))
			.andExpect(jsonPath("$.manejaInventario").value(false))
			.andReturn());

		mvc.perform(json(put("/api/v1/productos/" + sobre + "/receta").cookie(admin),
				"{\"componentes\":[{\"item\":\"" + papel + "\",\"cantidad\":1},{\"item\":\"" + goma
						+ "\",\"cantidad\":2.5,\"merma\":10}]}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.version").value(1))
			.andExpect(jsonPath("$.componentes", hasSize(2)));
		mvc.perform(get("/api/v1/productos/" + sobre + "/receta").cookie(admin))
			.andExpect(jsonPath("$.componentes[*].nombre", hasItem("Goma líquida")));
		mvc.perform(get("/api/v1/productos/recetas").cookie(admin))
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].nombre").value("Sobre de diezmos"))
			.andExpect(jsonPath("$[0].cantidadComponentes").value(2))
			.andExpect(jsonPath("$[0].componentes").value("Goma líquida 2.5 mL, Papel bond carta 1 hoja"));

		mvc.perform(get("/api/v1/productos?soloComponentes=true").cookie(admin))
			.andExpect(jsonPath("$.content[*].nombre", not(hasItem("Sobre de diezmos"))));
		mvc.perform(json(put("/api/v1/productos/" + sobre + "/receta").cookie(admin), "{\"componentes\":[]}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void productoVendidoPorMetros() throws Exception {
		mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"SIMPLE","nombre":"Cinta satinada","unidad":"MTR","permiteFraccion":true,"precioVenta":800}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.unidadSimbolo").value("m"))
			.andExpect(jsonPath("$.permiteFraccion").value(true));
		mvc.perform(json(post("/api/v1/productos").cookie(admin), """
				{"tipo":"SIMPLE","nombre":"Taza","unidad":"94","permiteFraccion":true,"precioVenta":800}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("La unidad Unidad no admite cantidades con decimales"));
	}

	@Test
	void categoriasMarcasLogoYFotoDePerfil() throws Exception {
		mvc.perform(json(post("/api/v1/productos/categorias").cookie(admin), "{\"nombre\":\"Papelería\"}"))
			.andExpect(jsonPath("$", hasSize(1)));
		mvc.perform(json(post("/api/v1/productos/marcas").cookie(admin), "{\"nombre\":\"Norma\"}"))
			.andExpect(jsonPath("$[0].nombre").value("Norma"));
		MvcResult foto = mvc.perform(multipart("/api/v1/empresa/logo")
			.file(new MockMultipartFile("imagen", "logo.png", "image/png", PNG))
			.cookie(admin)).andExpect(status().isOk()).andReturn();
		String archivo = foto.getResponse().getContentAsString().split("\"codigo\":\"")[1].substring(0, 12);
		mvc.perform(get("/api/v1/archivos/" + archivo).cookie(admin))
			.andExpect(status().isOk())
			.andExpect(content().contentType("image/png"));

		mvc.perform(multipart("/api/v1/auth/foto")
			.file(new MockMultipartFile("imagen", "falso.png", "image/png", "no soy imagen".getBytes()))
			.cookie(admin))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Solo se permiten imágenes PNG, JPG o WEBP"));

		mvc.perform(multipart("/api/v1/auth/foto").file(new MockMultipartFile("imagen", "yo.png", "image/png", PNG))
			.cookie(admin)).andExpect(status().isOk());
		mvc.perform(multipart("/api/v1/empresa/logo").file(new MockMultipartFile("imagen", "logo.png", "image/png", PNG))
			.cookie(admin)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/auth/yo").cookie(admin))
			.andExpect(jsonPath("$.foto").isNotEmpty())
			.andExpect(jsonPath("$.logo").isNotEmpty());
	}

	private String codigo(MvcResult resultado) throws Exception {
		return resultado.getResponse().getContentAsString().split("\"codigo\":\"")[1].substring(0, 12);
	}

	private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder peticion, String cuerpo) {
		return peticion.contentType(MediaType.APPLICATION_JSON).content(cuerpo);
	}

}
