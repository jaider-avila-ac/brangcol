package co.brangcol.impuesto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.impuesto.model.Impuesto;

@Repository
public class ImpuestoRepository {

	private final JdbcTemplate jdbc;

	public ImpuestoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Impuesto> activos() {
		return jdbc.query("""
				select codigo, nombre, tipo, tarifa from impuesto.impuesto
				where activo and valor_fijo is null
				order by tarifa desc, nombre
				""", new ImpuestoMapper());
	}

	private static final class ImpuestoMapper implements RowMapper<Impuesto> {

		@Override
		public Impuesto mapRow(ResultSet rs, int fila) throws SQLException {
			return new Impuesto(rs.getString("codigo"), rs.getString("nombre"), rs.getString("tipo"),
					rs.getBigDecimal("tarifa"));
		}

	}

}
