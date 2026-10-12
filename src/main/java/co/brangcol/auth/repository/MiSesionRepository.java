package co.brangcol.auth.repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;

@Repository
public class MiSesionRepository {

	private final JdbcTemplate jdbc;

	public MiSesionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public SesionUsuario miSesion() {
		return jdbc.queryForObject("select * from plataforma.mi_sesion()", new SesionUsuarioMapper());
	}

	public List<SesionAbierta> misSesiones() {
		return jdbc.query("select * from plataforma.mis_sesiones()", new SesionAbiertaMapper());
	}

	public int cerrarMisSesiones(String codigo) {
		Integer total = jdbc.queryForObject("select plataforma.cerrar_mis_sesiones(?)", Integer.class, codigo);
		return total == null ? 0 : total;
	}

	private static List<String> lista(Array arreglo) throws SQLException {
		List<String> valores = new ArrayList<>();
		if (arreglo == null) {
			return valores;
		}
		Object[] elementos = (Object[]) arreglo.getArray();
		for (Object elemento : elementos) {
			valores.add((String) elemento);
		}
		return valores;
	}

	private static final class SesionUsuarioMapper implements RowMapper<SesionUsuario> {

		@Override
		public SesionUsuario mapRow(ResultSet rs, int fila) throws SQLException {
			return new SesionUsuario(rs.getString("persona_codigo"), rs.getString("correo"), rs.getString("nombres"),
					rs.getString("apellidos"), rs.getString("empresa_codigo"), rs.getString("razon_social"),
					rs.getString("nombre_comercial"), rs.getString("empresa_estado"), rs.getString("rol_nombre"),
					rs.getString("rol_clave"), rs.getBoolean("es_propietario"), rs.getString("plan_codigo"),
					rs.getString("plan_nombre"), rs.getString("suscripcion_estado"), rs.getInt("minutos_inactividad"),
					lista(rs.getArray("funcionalidades")), lista(rs.getArray("permisos")));
		}

	}

	private static final class SesionAbiertaMapper implements RowMapper<SesionAbierta> {

		@Override
		public SesionAbierta mapRow(ResultSet rs, int fila) throws SQLException {
			return new SesionAbierta(rs.getString("codigo"), rs.getString("empresa"), rs.getString("ip"),
					rs.getString("user_agent"), rs.getObject("iniciada_en", OffsetDateTime.class),
					rs.getObject("ultima_actividad", OffsetDateTime.class), rs.getBoolean("es_actual"));
		}

	}

}
