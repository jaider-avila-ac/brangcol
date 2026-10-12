package co.brangcol.usuario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.usuario.model.InvitacionCreada;
import co.brangcol.usuario.model.InvitacionPendiente;

@Repository
public class InvitacionRepository {

	private final JdbcTemplate jdbc;

	public InvitacionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public InvitacionCreada crear(String correo, String rolCodigo) {
		return jdbc.queryForObject("""
				select i.invitacion_codigo, i.enlace_token, i.vence_en,
				       (select coalesce(e.nombre_comercial, e.razon_social) from plataforma.empresa e) as empresa,
				       (select r.nombre from plataforma.rol r where r.codigo = ?) as rol,
				       (select p.nombres from plataforma.persona p where p.id = nucleo.persona_actual()) as invitado_por,
				       lower(btrim(?)) as correo
				from plataforma.crear_invitacion(?, ?) i
				""", new InvitacionCreadaMapper(), rolCodigo, correo, correo, rolCodigo);
	}

	public List<InvitacionPendiente> pendientes() {
		return jdbc.query("""
				select i.codigo, i.correo, r.nombre as rol, i.expira_en, i.creado_en
				from plataforma.invitacion i
				join plataforma.rol r on r.empresa_id = i.empresa_id and r.id = i.rol_id
				where i.aceptada_en is null and i.revocada_en is null and i.expira_en > now()
				order by i.creado_en desc
				""", new InvitacionPendienteMapper());
	}

	public void revocar(String codigo) {
		jdbc.queryForObject("select 1 from plataforma.revocar_invitacion(?)", Integer.class, codigo);
	}

	private static final class InvitacionCreadaMapper implements RowMapper<InvitacionCreada> {

		@Override
		public InvitacionCreada mapRow(ResultSet rs, int fila) throws SQLException {
			return new InvitacionCreada(rs.getString("invitacion_codigo"), rs.getString("correo"),
					rs.getString("enlace_token"), rs.getObject("vence_en", OffsetDateTime.class),
					rs.getString("empresa"), rs.getString("rol"), rs.getString("invitado_por"));
		}

	}

	private static final class InvitacionPendienteMapper implements RowMapper<InvitacionPendiente> {

		@Override
		public InvitacionPendiente mapRow(ResultSet rs, int fila) throws SQLException {
			return new InvitacionPendiente(rs.getString("codigo"), rs.getString("correo"), rs.getString("rol"),
					rs.getObject("expira_en", OffsetDateTime.class), rs.getObject("creado_en", OffsetDateTime.class));
		}

	}

}
