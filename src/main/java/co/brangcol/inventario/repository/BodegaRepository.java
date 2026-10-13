package co.brangcol.inventario.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.inventario.model.Bodega;
import co.brangcol.inventario.model.DatosBodega;

@Repository
public class BodegaRepository {

	private static final String SELECT = """
			select b.codigo, b.nombre, s.nombre as sede, b.es_predeterminada, b.activa
			from inventario.bodega b
			join plataforma.sede s on s.empresa_id = b.empresa_id and s.id = b.sede_id
			""";

	private final JdbcTemplate jdbc;

	public BodegaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Bodega> listar() {
		return jdbc.query(SELECT + " order by b.es_predeterminada desc, b.nombre", (rs, n) -> new Bodega(
				rs.getString("codigo"), rs.getString("nombre"), rs.getString("sede"),
				rs.getBoolean("es_predeterminada"), rs.getBoolean("activa")));
	}

	// Sin sede indicada la bodega queda en la sede principal
	public int crear(DatosBodega datos) {
		return jdbc.update("""
				insert into inventario.bodega (sede_id, nombre)
				select s.id, ? from plataforma.sede s
				where s.codigo = ?::varchar or (?::varchar is null and s.es_principal)
				""", datos.nombre(), datos.sede(), datos.sede());
	}

	public int actualizar(String codigo, DatosBodega datos) {
		return jdbc.update("update inventario.bodega set nombre = ?, activa = ? where codigo = ?", datos.nombre(),
				datos.activa(), codigo);
	}

	public boolean nombreUsado(String nombre, String exceptoCodigo) {
		Boolean usado = jdbc.queryForObject("""
				select exists (select 1 from inventario.bodega where lower(nombre) = lower(?)
				               and codigo is distinct from ?::varchar)
				""", Boolean.class, nombre, exceptoCodigo);
		return Boolean.TRUE.equals(usado);
	}

}
