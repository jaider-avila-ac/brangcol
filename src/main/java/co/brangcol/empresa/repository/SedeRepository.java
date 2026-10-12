package co.brangcol.empresa.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.empresa.model.DatosSede;
import co.brangcol.empresa.model.Sede;

@Repository
public class SedeRepository {

	private static final String CONSULTA = """
			select s.codigo, s.nombre, s.direccion, m.codigo_dane, m.nombre as municipio, s.telefono, s.es_principal, s.activa
			from plataforma.sede s
			join parametro.municipio m on m.id = s.municipio_id
			""";

	private final JdbcTemplate jdbc;

	public SedeRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Sede> listar() {
		return jdbc.query(CONSULTA + " order by s.es_principal desc, s.nombre", new SedeMapper());
	}

	public Sede obtener(String codigo) {
		List<Sede> sedes = jdbc.query(CONSULTA + " where s.codigo = ?", new SedeMapper(), codigo);
		return sedes.isEmpty() ? null : sedes.get(0);
	}

	public String crear(DatosSede datos) {
		return jdbc.queryForObject("""
				insert into plataforma.sede (nombre, direccion, municipio_id, telefono, activa)
				values (?, ?, (select id from parametro.municipio where codigo_dane = ?), ?, ?)
				returning codigo
				""", String.class, datos.nombre(), datos.direccion(), datos.municipioCodigo(), datos.telefono(),
				datos.activa());
	}

	public int actualizar(String codigo, DatosSede datos) {
		return jdbc.update("""
				update plataforma.sede
				set nombre = ?, direccion = ?, municipio_id = (select id from parametro.municipio where codigo_dane = ?),
				    telefono = ?, activa = ?
				where codigo = ?
				""", datos.nombre(), datos.direccion(), datos.municipioCodigo(), datos.telefono(), datos.activa(),
				codigo);
	}

	private static final class SedeMapper implements RowMapper<Sede> {

		@Override
		public Sede mapRow(ResultSet rs, int fila) throws SQLException {
			return new Sede(rs.getString("codigo"), rs.getString("nombre"), rs.getString("direccion"),
					rs.getString("codigo_dane"), rs.getString("municipio"), rs.getString("telefono"),
					rs.getBoolean("es_principal"), rs.getBoolean("activa"));
		}

	}

}
