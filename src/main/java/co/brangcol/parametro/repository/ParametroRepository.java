package co.brangcol.parametro.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.parametro.model.Municipio;

@Repository
public class ParametroRepository {

	private static final String MUNICIPIOS = """
			select m.codigo_dane, m.nombre, d.nombre as departamento
			from parametro.municipio m
			join parametro.departamento d on d.id = m.departamento_id
			""";

	private final JdbcTemplate jdbc;

	public ParametroRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Municipio> buscarMunicipios(String texto) {
		return jdbc.query(MUNICIPIOS + """
				where unaccent(m.nombre) ilike unaccent('%' || ? || '%') or m.codigo_dane = ?
				order by m.nombre
				limit 20
				""", new MunicipioMapper(), texto, texto);
	}

	public boolean existeMunicipio(String codigo) {
		Boolean existe = jdbc.queryForObject("select exists (select 1 from parametro.municipio where codigo_dane = ?)",
				Boolean.class, codigo);
		return Boolean.TRUE.equals(existe);
	}

	private static final class MunicipioMapper implements RowMapper<Municipio> {

		@Override
		public Municipio mapRow(ResultSet rs, int fila) throws SQLException {
			return new Municipio(rs.getString("codigo_dane"), rs.getString("nombre"), rs.getString("departamento"));
		}

	}

}
