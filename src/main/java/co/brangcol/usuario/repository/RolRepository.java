package co.brangcol.usuario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import co.brangcol.usuario.model.PermisoRol;
import co.brangcol.usuario.model.Rol;

@Repository
public class RolRepository {

	private final JdbcTemplate jdbc;

	public RolRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Rol> listar() {
		return jdbc.query("""
				select r.codigo, r.nombre, r.descripcion, r.clave_sistema, p.codigo as permiso, p.descripcion as permiso_descripcion
				from plataforma.rol r
				left join plataforma.rol_permiso rp on rp.empresa_id = r.empresa_id and rp.rol_id = r.id
				left join plataforma.permiso p on p.codigo = rp.permiso
				where r.activo
				order by r.nombre, p.codigo
				""", new RolesExtractor());
	}

	public boolean existeActivo(String codigo) {
		Boolean existe = jdbc.queryForObject(
				"select exists (select 1 from plataforma.rol where codigo = ? and activo)", Boolean.class, codigo);
		return Boolean.TRUE.equals(existe);
	}

	private static final class RolesExtractor implements ResultSetExtractor<List<Rol>> {

		@Override
		public List<Rol> extractData(ResultSet rs) throws SQLException {
			Map<String, Rol> roles = new LinkedHashMap<>();
			while (rs.next()) {
				String codigo = rs.getString("codigo");
				Rol rol = roles.get(codigo);
				if (rol == null) {
					rol = new Rol(codigo, rs.getString("nombre"), rs.getString("descripcion"),
							rs.getString("clave_sistema"), new ArrayList<>());
					roles.put(codigo, rol);
				}
				String permiso = rs.getString("permiso");
				if (permiso != null) {
					rol.permisos().add(new PermisoRol(permiso, rs.getString("permiso_descripcion")));
				}
			}
			return new ArrayList<>(roles.values());
		}

	}

}
