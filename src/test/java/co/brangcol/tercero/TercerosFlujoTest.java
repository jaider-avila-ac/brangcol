package co.brangcol.tercero;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
class TercerosFlujoTest extends PruebaIntegracion {

	private static int contador = 0;

	@Autowired
	private MockMvc mvc;

	private Cookie admin;

	@BeforeEach
	void iniciar() throws Exception {
		contador++;
		String correo = "terceros" + contador + "@prueba.co";
		DatosPrueba.crearEmpresa(correo);
		MvcResult login = mvc.perform(json(post("/api/v1/auth/login"),
				"{\"correo\":\"" + correo + "\",\"contrasena\":\"" + DatosPrueba.CLAVE + "\"}"))
			.andExpect(status().isOk())
			.andReturn();
		Cookie cookie = login.getResponse().getCookie("bc_sesion");
		assertNotNull(cookie);
		admin = new Cookie("bc_sesion", cookie.getValue());
	}

	@Test
	void unMismoNitEsProveedorYCliente() throws Exception {
		String codigo = codigo(mvc.perform(json(post("/api/v1/terceros").cookie(admin), """
				{"tipoPersona":"JURIDICA","tipoDocumento":"31","numeroDocumento":"900.123.456","razonSocial":"Ferretería El Tornillo SAS",
				 "telefono":"3001234567","municipio":"08001","proveedor":{"plazoDias":30}}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.numeroDocumento").value("900123456"))
			.andExpect(jsonPath("$.dv").isNotEmpty())
			.andExpect(jsonPath("$.proveedor.plazoDias").value(30))
			.andExpect(jsonPath("$.cliente").isEmpty())
			.andReturn());

		mvc.perform(get("/api/v1/terceros/documento?tipo=31&numero=900123456").cookie(admin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigo").value(codigo))
			.andExpect(jsonPath("$.esProveedor").value(true));

		mvc.perform(json(post("/api/v1/terceros").cookie(admin), """
				{"tipoPersona":"JURIDICA","tipoDocumento":"31","numeroDocumento":"900123456","razonSocial":"Copia","cliente":{"plazoDias":0}}
				"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Ya está registrado Ferretería El Tornillo SAS con ese documento"));

		mvc.perform(json(put("/api/v1/terceros/" + codigo).cookie(admin), """
				{"tipoPersona":"JURIDICA","tipoDocumento":"31","numeroDocumento":"900123456","razonSocial":"Ferretería El Tornillo SAS",
				 "telefono":"3001234567","municipio":"08001","proveedor":{"plazoDias":30},"cliente":{"plazoDias":15,"cupoCredito":2000000,"controlCredito":"ADVERTIR"}}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cliente.plazoDias").value(15))
			.andExpect(jsonPath("$.proveedor.plazoDias").value(30));

		mvc.perform(get("/api/v1/terceros?rol=CLIENTES").cookie(admin)).andExpect(jsonPath("$.content", hasSize(1)));
		mvc.perform(get("/api/v1/terceros?rol=PROVEEDORES").cookie(admin)).andExpect(jsonPath("$.content", hasSize(1)));
	}

	@Test
	void laBusquedaToleraErroresYTildes() throws Exception {
		crearCliente("Juan", "Pérez Gómez", "1045678901");
		crearCliente("María", "López", "22334455");

		mvc.perform(get("/api/v1/terceros?texto=jaun perz").cookie(admin))
			.andExpect(jsonPath("$.content[0].nombre").value("Juan Pérez Gómez"));
		mvc.perform(get("/api/v1/terceros?texto=maria").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].nombre").value("María López"));
		mvc.perform(get("/api/v1/terceros?texto=104567").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].documento").value("CC 1045678901"));
		mvc.perform(get("/api/v1/terceros?soloDeben=true").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(0)));
		mvc.perform(get("/api/v1/terceros?tamano=1&pagina=1").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.totalPages").value(2));
	}

	@Test
	void validacionesConMensajesClaros() throws Exception {
		mvc.perform(json(post("/api/v1/terceros").cookie(admin), """
				{"tipoPersona":"JURIDICA","tipoDocumento":"13","numeroDocumento":"123456","razonSocial":"Mal SAS","cliente":{"plazoDias":0}}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Una empresa se registra con NIT"));
		mvc.perform(json(post("/api/v1/terceros").cookie(admin), """
				{"tipoPersona":"NATURAL","nombres":"Sin Rol","tipoDocumento":"13","numeroDocumento":"77665544"}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Marque si es cliente, proveedor o ambos"));
		mvc.perform(json(post("/api/v1/terceros").cookie(admin), """
				{"tipoPersona":"NATURAL","nombres":"Correo Malo","correo":"no-es-correo","cliente":{"plazoDias":0}}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("El correo no es válido"));
	}

	@Test
	void importarDesdeExcel() throws Exception {
		byte[] archivo = excel(List.of(
				List.of("CC", "1.111.111", "Ana", "Ruiz", "", "", "3001112233", "ana@correo.co", "Calle 1", "barranquilla", "SI", "", "0"),
				List.of("NIT", "900111222", "", "", "Distribuidora Uno SAS", "", "", "", "", "", "NO", "SI", "30"),
				List.of("CC", "1111111", "Ana", "Repetida", "", "", "", "", "", "", "", "", ""),
				List.of("CC", "2222222", "Luis", "", "", "", "", "correo-malo", "", "", "", "", ""),
				List.of("XX", "3333333", "Pedro", "", "", "", "", "", "", "", "", "", "")));
		mvc.perform(multipart("/api/v1/terceros/importar").file(new MockMultipartFile("archivo", "t.xlsx",
				"application/octet-stream", archivo)).cookie(admin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.creados").value(2))
			.andExpect(jsonPath("$.actualizados").value(0))
			.andExpect(jsonPath("$.errores", hasSize(3)))
			.andExpect(jsonPath("$.errores[0].fila").value(4))
			.andExpect(jsonPath("$.errores[0].mensaje").value("El documento está repetido en el archivo"));

		mvc.perform(get("/api/v1/terceros?texto=ana ruiz").cookie(admin))
			.andExpect(jsonPath("$.content[0].municipio").value("Barranquilla"));

		byte[] segundo = excel(List.of(
				List.of("NIT", "900111222", "", "", "Distribuidora Uno SAS", "", "", "", "", "", "SI", "SI", "10")));
		mvc.perform(multipart("/api/v1/terceros/importar").file(new MockMultipartFile("archivo", "t.xlsx",
				"application/octet-stream", segundo)).cookie(admin))
			.andExpect(jsonPath("$.creados").value(0))
			.andExpect(jsonPath("$.actualizados").value(1));
		mvc.perform(get("/api/v1/terceros?rol=CLIENTES&texto=distribuidora").cookie(admin))
			.andExpect(jsonPath("$.content", hasSize(1)));
	}

	@Test
	void descargaLaPlantilla() throws Exception {
		MvcResult resultado = mvc.perform(get("/api/v1/terceros/plantilla").cookie(admin))
			.andExpect(status().isOk())
			.andReturn();
		assertTrue(resultado.getResponse().getContentAsByteArray().length > 1000);
		assertTrue(resultado.getResponse().getHeader("Content-Disposition").contains("plantilla_clientes_proveedores.xlsx"));
	}

	private void crearCliente(String nombres, String apellidos, String documento) throws Exception {
		mvc.perform(json(post("/api/v1/terceros").cookie(admin),
				"{\"tipoPersona\":\"NATURAL\",\"tipoDocumento\":\"13\",\"numeroDocumento\":\"" + documento
						+ "\",\"nombres\":\"" + nombres + "\",\"apellidos\":\"" + apellidos
						+ "\",\"cliente\":{\"plazoDias\":0}}"))
			.andExpect(status().isCreated());
	}

	private byte[] excel(List<List<String>> filas) throws Exception {
		try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
			Sheet hoja = libro.createSheet();
			hoja.createRow(0).createCell(0).setCellValue("Tipo de documento");
			for (int i = 0; i < filas.size(); i++) {
				Row fila = hoja.createRow(i + 1);
				for (int c = 0; c < filas.get(i).size(); c++) {
					fila.createCell(c).setCellValue(filas.get(i).get(c));
				}
			}
			libro.write(salida);
			return salida.toByteArray();
		}
	}

	private String codigo(MvcResult resultado) throws Exception {
		return resultado.getResponse().getContentAsString().split("\"codigo\":\"")[1].substring(0, 12);
	}

	private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder peticion, String cuerpo) {
		return peticion.contentType(MediaType.APPLICATION_JSON).content(cuerpo);
	}

}
