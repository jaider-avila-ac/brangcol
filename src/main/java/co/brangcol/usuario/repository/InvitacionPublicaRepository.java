package co.brangcol.usuario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.usuario.model.Aceptacion;
import co.brangcol.usuario.model.InvitacionPublica;

@Repository
public class InvitacionPublicaRepository {

	private final JdbcTemplate jdbc;

	public InvitacionPublicaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public InvitacionPublica consultar(String token) {
		return jdbc.queryForObject("select * from plataforma.ver_invitacion(?)", new InvitacionPublicaMapper(),
				token);
	}

	public void aceptar(Aceptacion aceptacion) {
		jdbc.queryForObject("select plataforma.aceptar_invitacion(?, ?, ?, ?)", String.class, aceptacion.token(),
				aceptacion.nombres(), aceptacion.apellidos(), aceptacion.contrasena());
	}

	private static final class InvitacionPublicaMapper implements RowMapper<InvitacionPublica> {

		@Override
		public InvitacionPublica mapRow(ResultSet rs, int fila) throws SQLException {
			return new InvitacionPublica(rs.getString("empresa"), rs.getString("correo"), rs.getString("rol"),
					rs.getBoolean("tiene_cuenta"), rs.getObject("vence_en", OffsetDateTime.class));
		}

	}

}
