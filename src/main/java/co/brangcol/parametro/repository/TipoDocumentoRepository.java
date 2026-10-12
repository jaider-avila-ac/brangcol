package co.brangcol.parametro.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.parametro.model.TipoDocumento;

@Repository
public class TipoDocumentoRepository {

	private final JdbcTemplate jdbc;

	public TipoDocumentoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<TipoDocumento> todos() {
		return jdbc.query("""
				select codigo_dian, abreviatura, nombre, aplica_a
				from parametro.tipo_documento_identidad
				order by case codigo_dian when '13' then 0 when '31' then 1 else 2 end, nombre
				""", new TipoDocumentoMapper());
	}

	private static final class TipoDocumentoMapper implements RowMapper<TipoDocumento> {

		@Override
		public TipoDocumento mapRow(ResultSet rs, int fila) throws SQLException {
			return new TipoDocumento(rs.getString("codigo_dian"), rs.getString("abreviatura"), rs.getString("nombre"),
					rs.getString("aplica_a"));
		}

	}

}
