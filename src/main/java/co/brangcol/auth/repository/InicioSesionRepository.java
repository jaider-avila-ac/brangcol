package co.brangcol.auth.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.EstadoInicio;
import co.brangcol.auth.model.ResultadoInicio;

@Repository
public class InicioSesionRepository {

	private final JdbcTemplate jdbc;

	public InicioSesionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public ResultadoInicio iniciarSesion(String correo, String contrasena, String empresa, String ip, String agente) {
		return jdbc.queryForObject(
				"select estado, token, expira_en from plataforma.iniciar_sesion(?, ?, ?, ?::inet, ?)",
				new ResultadoInicioMapper(), correo, contrasena, empresa, ip, agente);
	}

	public List<EmpresaDisponible> listarEmpresas(String correo, String contrasena, String ip, String agente) {
		return jdbc.query(
				"select empresa_codigo, razon_social, rol from plataforma.listar_empresas(?, ?, ?::inet, ?)",
				new EmpresaDisponibleMapper(), correo, contrasena, ip, agente);
	}

	public void cerrarSesion(String token) {
		jdbc.queryForObject("select 1 from plataforma.cerrar_sesion(?)", Integer.class, token);
	}

	private static final class ResultadoInicioMapper implements RowMapper<ResultadoInicio> {

		@Override
		public ResultadoInicio mapRow(ResultSet rs, int fila) throws SQLException {
			return new ResultadoInicio(EstadoInicio.valueOf(rs.getString("estado")), rs.getString("token"),
					rs.getObject("expira_en", OffsetDateTime.class));
		}

	}

	private static final class EmpresaDisponibleMapper implements RowMapper<EmpresaDisponible> {

		@Override
		public EmpresaDisponible mapRow(ResultSet rs, int fila) throws SQLException {
			return new EmpresaDisponible(rs.getString("empresa_codigo"), rs.getString("razon_social"),
					rs.getString("rol"));
		}

	}

}
