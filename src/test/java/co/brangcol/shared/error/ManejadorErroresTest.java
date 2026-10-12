package co.brangcol.shared.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import co.brangcol.PruebaIntegracion;

@AutoConfigureMockMvc
class ManejadorErroresTest extends PruebaIntegracion {

	@Autowired
	private MockMvc mvc;

	@Test
	void errorDeNegocioUsaElMensajeDeLaBase() throws Exception {
		mvc.perform(get("/prueba-errores/base/BC020"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Mensaje desde la base"));
	}

	@Test
	void sesionInvalidaResponde401() throws Exception {
		mvc.perform(get("/prueba-errores/base/BC113")).andExpect(status().isUnauthorized());
	}

	@Test
	void sinPermisoResponde403() throws Exception {
		mvc.perform(get("/prueba-errores/base/BC115")).andExpect(status().isForbidden());
	}

	@Test
	void registroDuplicadoResponde409() throws Exception {
		mvc.perform(get("/prueba-errores/base/23505"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Ya existe un registro con esos datos"));
	}

	@Test
	void errorInesperadoNoMuestraDetalles() throws Exception {
		mvc.perform(get("/prueba-errores/inesperado"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message")
				.value("Ocurrió un error inesperado. Intente de nuevo o informe al administrador."));
	}

	@Test
	void validacionDevuelveElMensajeDelCampo() throws Exception {
		mvc.perform(post("/prueba-errores/validacion").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
	}

	@Test
	void rutaInexistenteResponde404() throws Exception {
		mvc.perform(get("/no-existe"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Recurso no encontrado"));
	}

}
