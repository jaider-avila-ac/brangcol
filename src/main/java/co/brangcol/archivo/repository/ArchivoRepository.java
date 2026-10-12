package co.brangcol.archivo.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.archivo.model.DatosArchivo;

@Repository
public class ArchivoRepository {

	private final JdbcTemplate jdbc;

	public ArchivoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	// RLS deja ver solo los archivos de la empresa de la sesion
	public DatosArchivo buscar(String codigo) {
		List<DatosArchivo> archivos = jdbc.query("""
				select nombre_original, ruta, tipo_mime from transversal.archivo
				where codigo = ? and eliminado_en is null
				""", new DatosArchivoMapper(), codigo);
		return archivos.isEmpty() ? null : archivos.get(0);
	}

	private static final class DatosArchivoMapper implements RowMapper<DatosArchivo> {

		@Override
		public DatosArchivo mapRow(ResultSet rs, int fila) throws SQLException {
			return new DatosArchivo(rs.getString("nombre_original"), rs.getString("ruta"), rs.getString("tipo_mime"));
		}

	}

}
