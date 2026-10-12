package co.brangcol;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.flywaydb.core.Flyway;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
public abstract class PruebaIntegracion {

	protected static final String CLAVE_DUENO = "dueno_prueba_123";
	static final String CLAVE_APP = "app_prueba_123";
	static final String CLAVE_WORKER = "worker_prueba_123";

	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine")
		.withEnv("TZ", "America/Bogota")
		.withEnv("PGTZ", "America/Bogota");

	static {
		POSTGRES.start();
		crearDueno();
		migrar();
	}

	@DynamicPropertySource
	static void propiedades(DynamicPropertyRegistry registro) {
		registro.add("spring.datasource.url", PruebaIntegracion::url);
		registro.add("spring.datasource.username", PruebaIntegracion::usuarioApp);
		registro.add("spring.datasource.password", PruebaIntegracion::claveApp);
	}

	static String url() {
		return "jdbc:postgresql://" + POSTGRES.getHost() + ":" + POSTGRES.getMappedPort(5432) + "/brangcol";
	}

	static String usuarioApp() {
		return "brangcol_app";
	}

	static String claveApp() {
		return CLAVE_APP;
	}

	protected static Connection conectar(String usuario, String clave) throws SQLException {
		return DriverManager.getConnection(url(), usuario, clave);
	}

	private static void crearDueno() {
		try (Connection con = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
				POSTGRES.getPassword()); Statement st = con.createStatement()) {
			st.execute("CREATE ROLE brangcol_owner LOGIN NOSUPERUSER CREATEROLE BYPASSRLS PASSWORD '" + CLAVE_DUENO
					+ "'");
			st.execute("CREATE DATABASE brangcol OWNER brangcol_owner");
		}
		catch (SQLException ex) {
			throw new IllegalStateException("No se pudo preparar la base de pruebas", ex);
		}
	}

	private static void migrar() {
		Path ruta = Path.of(System.getProperty("brangcol.migraciones", "../brangol_basedatos/migraciones"))
			.toAbsolutePath()
			.normalize();
		if (!Files.isDirectory(ruta)) {
			throw new IllegalStateException("No existe la carpeta de migraciones: " + ruta);
		}
		Map<String, String> placeholders = new HashMap<>();
		placeholders.put("app_password", CLAVE_APP);
		placeholders.put("worker_password", CLAVE_WORKER);
		Flyway.configure()
			.dataSource(url(), "brangcol_owner", CLAVE_DUENO)
			.locations("filesystem:" + ruta)
			.placeholders(placeholders)
			.load()
			.migrate();
	}

}
