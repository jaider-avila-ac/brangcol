package co.brangcol.producto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.Marca;

@Repository
public class MarcaRepository {

	private final JdbcTemplate jdbc;

	public MarcaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Marca> listar() {
		return jdbc.query("select codigo, nombre, activa from producto.marca order by nombre", new MarcaMapper());
	}

	public String crear(String nombre) {
		return jdbc.queryForObject("insert into producto.marca (nombre) values (?) returning codigo", String.class,
				nombre);
	}

	public int actualizar(String codigo, String nombre, boolean activa) {
		return jdbc.update("update producto.marca set nombre = ?, activa = ? where codigo = ?", nombre, activa, codigo);
	}

	public boolean existe(String codigo) {
		Boolean existe = jdbc.queryForObject("select exists (select 1 from producto.marca where codigo = ?)",
				Boolean.class, codigo);
		return Boolean.TRUE.equals(existe);
	}

	private static final class MarcaMapper implements RowMapper<Marca> {

		@Override
		public Marca mapRow(ResultSet rs, int fila) throws SQLException {
			return new Marca(rs.getString("codigo"), rs.getString("nombre"), rs.getBoolean("activa"));
		}

	}

}
