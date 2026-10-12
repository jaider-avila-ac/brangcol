package co.brangcol.parametro.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.parametro.model.Unidad;

@Repository
public class UnidadRepository {

	private final JdbcTemplate jdbc;

	public UnidadRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Unidad> todas() {
		return jdbc.query("""
				select codigo_dian, nombre, simbolo, magnitud, permite_fraccion
				from parametro.unidad_medida
				order by case codigo_dian when '94' then 0 else 1 end, nombre
				""", new UnidadMapper());
	}

	private static final class UnidadMapper implements RowMapper<Unidad> {

		@Override
		public Unidad mapRow(ResultSet rs, int fila) throws SQLException {
			return new Unidad(rs.getString("codigo_dian"), rs.getString("nombre"), rs.getString("simbolo"),
					rs.getString("magnitud"), rs.getBoolean("permite_fraccion"));
		}

	}

}
