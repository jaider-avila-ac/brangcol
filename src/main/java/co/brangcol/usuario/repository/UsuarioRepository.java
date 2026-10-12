package co.brangcol.usuario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.usuario.model.Usuario;

@Repository
public class UsuarioRepository {

	private static final String CONSULTA = """
			select m.codigo, p.nombres, p.apellidos, p.correo, r.codigo as rol_codigo, r.nombre as rol_nombre,
			       m.es_propietario, m.activa, p.ultimo_ingreso_en, m.persona_id = nucleo.persona_actual() as es_yo
			from plataforma.membresia m
			join plataforma.persona p on p.id = m.persona_id
			join plataforma.rol r on r.empresa_id = m.empresa_id and r.id = m.rol_id
			""";

	private final JdbcTemplate jdbc;

	public UsuarioRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Usuario> listar() {
		return jdbc.query(CONSULTA + " order by m.activa desc, p.nombres, p.apellidos", new UsuarioMapper());
	}

	public Usuario obtener(String codigo) {
		List<Usuario> usuarios = jdbc.query(CONSULTA + " where m.codigo = ?", new UsuarioMapper(), codigo);
		return usuarios.isEmpty() ? null : usuarios.get(0);
	}

	public int cambiarRol(String codigo, String rolCodigo) {
		return jdbc.update("""
				update plataforma.membresia
				set rol_id = (select id from plataforma.rol where codigo = ? and activo)
				where codigo = ?
				""", rolCodigo, codigo);
	}

	public int cambiarEstado(String codigo, boolean activo) {
		return jdbc.update("update plataforma.membresia set activa = ? where codigo = ?", activo, codigo);
	}

	private static final class UsuarioMapper implements RowMapper<Usuario> {

		@Override
		public Usuario mapRow(ResultSet rs, int fila) throws SQLException {
			return new Usuario(rs.getString("codigo"), rs.getString("nombres"), rs.getString("apellidos"),
					rs.getString("correo"), rs.getString("rol_codigo"), rs.getString("rol_nombre"),
					rs.getBoolean("es_propietario"), rs.getBoolean("activa"),
					rs.getObject("ultimo_ingreso_en", OffsetDateTime.class), rs.getBoolean("es_yo"));
		}

	}

}
