package co.brangcol.shared.sesion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import co.brangcol.DatosPrueba;
import co.brangcol.PruebaIntegracion;
import co.brangcol.shared.error.ErrorTraducido;
import co.brangcol.shared.error.TraductorErrorBaseDatos;
import jakarta.persistence.EntityManagerFactory;

class SesionTransactionManagerTest extends PruebaIntegracion {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private TraductorErrorBaseDatos traductor;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@AfterEach
	void limpiar() {
		SesionActual.limpiar();
	}

	@Test
	void sinSesionNoHayEmpresaNiDatos() {
		DatosPrueba.crearEmpresa("sin.sesion@prueba.co");
		assertNull(consultar(false, "select nucleo.empresa_actual()"));
		assertEquals(0L, consultar(false, "select count(*) from tercero.tercero"));
	}

	@Test
	void conSesionSoloSeVeLaPropiaEmpresa() {
		String token = DatosPrueba.empresaConSesion("empresa.a@prueba.co");
		DatosPrueba.crearEmpresa("empresa.b@prueba.co");
		SesionActual.establecer(token);
		assertNotNull(consultar(false, "select nucleo.empresa_actual()"));
		assertEquals(1L, consultar(false, "select count(distinct empresa_id) from tercero.tercero"));
	}

	@Test
	void funcionaEnTransaccionDeSoloLectura() {
		SesionActual.establecer(DatosPrueba.empresaConSesion("lectura@prueba.co"));
		assertNotNull(consultar(true, "select nucleo.empresa_actual()"));
	}

	@Test
	void tokenInvalidoResponde401YLiberaLaConexion() {
		SesionActual.establecer("token-que-no-existe");
		RuntimeException error = assertThrows(RuntimeException.class,
				() -> consultar(false, "select nucleo.empresa_actual()"));
		ErrorTraducido traducido = traductor.traducir(error);
		assertNotNull(traducido);
		assertEquals(HttpStatus.UNAUTHORIZED, traducido.estado());
		assertFalse(TransactionSynchronizationManager.hasResource(entityManagerFactory));

		SesionActual.establecer(DatosPrueba.empresaConSesion("despues.de.error@prueba.co"));
		assertNotNull(consultar(false, "select nucleo.empresa_actual()"));
	}

	private Object consultar(boolean soloLectura, String sql) {
		TransactionTemplate plantilla = new TransactionTemplate(transactionManager);
		plantilla.setReadOnly(soloLectura);
		return plantilla.execute(estado -> jdbc.queryForObject(sql, Object.class));
	}

}
