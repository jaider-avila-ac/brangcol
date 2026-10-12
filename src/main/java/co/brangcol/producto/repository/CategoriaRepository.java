package co.brangcol.producto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.producto.model.Categoria;

@Repository
public class CategoriaRepository {

	private static final String PADRE = "(select id from producto.categoria where codigo = ?)";

	private final JdbcTemplate jdbc;

	public CategoriaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Categoria> listar() {
		return jdbc.query("""
				select c.codigo, c.nombre, p.codigo as padre_codigo, p.nombre as padre_nombre, c.activa,
				       (select count(*) from producto.item i where i.empresa_id = c.empresa_id and i.categoria_id = c.id) as productos
				from producto.categoria c
				left join producto.categoria p on p.empresa_id = c.empresa_id and p.id = c.padre_id
				order by coalesce(p.nombre, c.nombre), p.nombre nulls first, c.nombre
				""", new CategoriaMapper());
	}

	public String crear(String nombre, String padre) {
		return jdbc.queryForObject("insert into producto.categoria (nombre, padre_id) values (?, " + PADRE
				+ ") returning codigo", String.class, nombre, padre);
	}

	public int actualizar(String codigo, String nombre, String padre, boolean activa) {
		return jdbc.update("update producto.categoria set nombre = ?, padre_id = " + PADRE + ", activa = ? where codigo = ?",
				nombre, padre, activa, codigo);
	}

	public boolean existe(String codigo) {
		Boolean existe = jdbc.queryForObject("select exists (select 1 from producto.categoria where codigo = ?)",
				Boolean.class, codigo);
		return Boolean.TRUE.equals(existe);
	}

	private static final class CategoriaMapper implements RowMapper<Categoria> {

		@Override
		public Categoria mapRow(ResultSet rs, int fila) throws SQLException {
			return new Categoria(rs.getString("codigo"), rs.getString("nombre"), rs.getString("padre_codigo"),
					rs.getString("padre_nombre"), rs.getBoolean("activa"), rs.getLong("productos"));
		}

	}

}
