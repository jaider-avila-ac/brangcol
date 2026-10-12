package co.brangcol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public final class DatosPrueba {

	public static final String CLAVE = "ClaveSegura2026";

	private DatosPrueba() {
	}

	public static String empresaConSesion(String correo) {
		crearEmpresa(correo);
		return iniciarSesion(correo);
	}

	public static void crearEmpresa(String correo) {
		String nit = String.valueOf(ThreadLocalRandom.current().nextLong(800000000L, 999999999L));
		String empresa = """
				{"tipo_persona":"JURIDICA","razon_social":"Empresa %s","tipo_documento":"31","numero_documento":"%s",
				 "responsable_iva":true,"direccion":"Calle 1","municipio":"08001","correo":"%s"}
				""".formatted(nit, nit, correo);
		String admin = """
				{"correo":"%s","nombres":"Admin","apellidos":"Pruebas"}
				""".formatted(correo);
		try (Connection con = PruebaIntegracion.conectar("brangcol_worker", PruebaIntegracion.CLAVE_WORKER)) {
			try (PreparedStatement ps = con.prepareStatement("select plataforma.crear_empresa(?::jsonb, ?::jsonb)")) {
				ps.setString(1, empresa);
				ps.setString(2, admin);
				ps.execute();
			}
			try (PreparedStatement ps = con.prepareStatement("select plataforma.establecer_contrasena(?, ?)")) {
				ps.setLong(1, idPersona(correo));
				ps.setString(2, CLAVE);
				ps.execute();
			}
		}
		catch (SQLException ex) {
			throw new IllegalStateException("No se pudo crear la empresa de prueba", ex);
		}
	}

	private static long idPersona(String correo) throws SQLException {
		try (Connection con = PruebaIntegracion.conectar("brangcol_owner", PruebaIntegracion.CLAVE_DUENO);
				PreparedStatement ps = con.prepareStatement("select id from plataforma.persona where correo = ?")) {
			ps.setString(1, correo);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getLong(1);
			}
		}
	}

	public static String iniciarSesion(String correo) {
		String sql = "select estado, token from plataforma.iniciar_sesion(?, ?, null, '127.0.0.1'::inet, 'pruebas')";
		try (Connection con = PruebaIntegracion.conectar("brangcol_app", PruebaIntegracion.CLAVE_APP);
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, correo);
			ps.setString(2, CLAVE);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				if (rs.getString("token") == null) {
					throw new IllegalStateException("Inicio de sesion rechazado: " + rs.getString("estado"));
				}
				return rs.getString("token");
			}
		}
		catch (SQLException ex) {
			throw new IllegalStateException("No se pudo iniciar sesion", ex);
		}
	}

}
