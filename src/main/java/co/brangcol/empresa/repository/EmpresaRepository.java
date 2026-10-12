package co.brangcol.empresa.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.empresa.model.DatosEmpresa;
import co.brangcol.empresa.model.Empresa;

@Repository
public class EmpresaRepository {

	private final JdbcTemplate jdbc;

	public EmpresaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Empresa obtener() {
		return jdbc.queryForObject("""
				select e.codigo, e.tipo_persona, e.razon_social, e.nombre_comercial, t.nombre as tipo_documento,
				       e.numero_documento, e.dv, e.responsable_iva, e.direccion, m.codigo_dane, m.nombre as municipio,
				       d.nombre as departamento, e.telefono, e.correo, e.estado
				from plataforma.empresa e
				join parametro.tipo_documento_identidad t on t.id = e.tipo_documento_id
				join parametro.municipio m on m.id = e.municipio_id
				join parametro.departamento d on d.id = m.departamento_id
				""", new EmpresaMapper());
	}

	public void actualizar(DatosEmpresa datos) {
		jdbc.update("""
				update plataforma.empresa
				set razon_social = ?, nombre_comercial = ?, responsable_iva = ?, direccion = ?,
				    municipio_id = (select id from parametro.municipio where codigo_dane = ?), telefono = ?, correo = ?
				""", datos.razonSocial(), datos.nombreComercial(), datos.responsableIva(), datos.direccion(),
				datos.municipioCodigo(), datos.telefono(), datos.correo());
	}

	private static final class EmpresaMapper implements RowMapper<Empresa> {

		@Override
		public Empresa mapRow(ResultSet rs, int fila) throws SQLException {
			return new Empresa(rs.getString("codigo"), rs.getString("tipo_persona"), rs.getString("razon_social"),
					rs.getString("nombre_comercial"), rs.getString("tipo_documento"),
					rs.getString("numero_documento"), rs.getString("dv"), rs.getBoolean("responsable_iva"),
					rs.getString("direccion"), rs.getString("codigo_dane"), rs.getString("municipio"),
					rs.getString("departamento"), rs.getString("telefono"), rs.getString("correo"),
					rs.getString("estado"));
		}

	}

}
