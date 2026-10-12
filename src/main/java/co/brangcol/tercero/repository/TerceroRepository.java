package co.brangcol.tercero.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.brangcol.tercero.model.Coincidencia;
import co.brangcol.tercero.model.CondicionesCliente;
import co.brangcol.tercero.model.CondicionesProveedor;
import co.brangcol.tercero.model.DatosTercero;
import co.brangcol.tercero.model.Tercero;

@Repository
public class TerceroRepository {

	private static final String TIPO_DOCUMENTO = "(select id from parametro.tipo_documento_identidad where codigo_dian = ?)";

	private static final String MUNICIPIO = "(select id from parametro.municipio where codigo_dane = ?)";

	private final JdbcTemplate jdbc;

	public TerceroRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String crear(DatosTercero d) {
		return jdbc.queryForObject("""
				insert into tercero.tercero (tipo_persona, tipo_documento_id, numero_documento, razon_social, nombres, apellidos,
				    nombre_comercial, responsable_iva, direccion, municipio_id, correo, telefono, notas, estado_datos)
				values (?, %s, ?, ?, ?, ?, ?, ?, ?, %s, ?, ?, ?, ?)
				returning codigo
				""".formatted(TIPO_DOCUMENTO, MUNICIPIO), String.class, d.tipoPersona(), d.tipoDocumento(),
				d.numeroDocumento(), d.razonSocial(), d.nombres(), d.apellidos(), d.nombreComercial(), d.responsableIva(),
				d.direccion(), d.municipio(), d.correo(), d.telefono(), d.notas(), estadoDatos(d));
	}

	public int actualizar(String codigo, DatosTercero d) {
		return jdbc.update("""
				update tercero.tercero
				set tipo_persona = ?, tipo_documento_id = %s, numero_documento = ?, razon_social = ?, nombres = ?, apellidos = ?,
				    nombre_comercial = ?, responsable_iva = ?, direccion = ?, municipio_id = %s, correo = ?, telefono = ?,
				    notas = ?, estado_datos = ?
				where codigo = ? and not es_consumidor_final
				""".formatted(TIPO_DOCUMENTO, MUNICIPIO), d.tipoPersona(), d.tipoDocumento(), d.numeroDocumento(),
				d.razonSocial(), d.nombres(), d.apellidos(), d.nombreComercial(), d.responsableIva(), d.direccion(),
				d.municipio(), d.correo(), d.telefono(), d.notas(), estadoDatos(d), codigo);
	}

	public int cambiarEstado(String codigo, boolean activo) {
		return jdbc.update("update tercero.tercero set activo = ? where codigo = ? and not es_consumidor_final", activo,
				codigo);
	}

	public Tercero obtener(String codigo) {
		List<Tercero> terceros = jdbc.query("""
				select t.codigo, t.tipo_persona, td.codigo_dian as tipo_documento, t.numero_documento, t.dv, t.razon_social,
				       t.nombres, t.apellidos, t.nombre_comercial, t.nombre_completo, t.responsable_iva, t.direccion,
				       m.codigo_dane, m.nombre as municipio, t.correo, t.telefono, t.notas, t.activo, t.estado_datos,
				       c.tercero_id is not null as es_cliente, c.plazo_dias as plazo_cliente, c.cupo_credito, c.control_credito,
				       p.tercero_id is not null as es_proveedor, p.plazo_dias as plazo_proveedor, b.nombre as banco,
				       p.tipo_cuenta, p.numero_cuenta,
				       coalesce(sc.total_debe, 0) as debe, coalesce(sc.saldo_a_favor, 0) as saldo_favor,
				       coalesce(cp.total, 0) as le_debemos
				from tercero.tercero t
				left join parametro.tipo_documento_identidad td on td.id = t.tipo_documento_id
				left join parametro.municipio m on m.id = t.municipio_id
				left join tercero.cliente c on c.empresa_id = t.empresa_id and c.tercero_id = t.id
				left join tercero.proveedor p on p.empresa_id = t.empresa_id and p.tercero_id = t.id
				left join parametro.banco b on b.id = p.banco_id
				left join reportes.v_saldo_cliente sc on sc.empresa_id = t.empresa_id and sc.tercero_id = t.id
				left join reportes.v_cuentas_por_pagar cp on cp.empresa_id = t.empresa_id and cp.tercero_id = t.id
				where t.codigo = ? and not t.es_consumidor_final
				""", new TerceroMapper(), codigo);
		return terceros.isEmpty() ? null : terceros.get(0);
	}

	public Coincidencia buscarPorDocumento(String tipoDocumento, String numero) {
		List<Coincidencia> coincidencias = jdbc.query("""
				select t.codigo, t.nombre_completo,
				       exists (select 1 from tercero.cliente c where c.empresa_id = t.empresa_id and c.tercero_id = t.id) as es_cliente,
				       exists (select 1 from tercero.proveedor p where p.empresa_id = t.empresa_id and p.tercero_id = t.id) as es_proveedor
				from tercero.tercero t
				where t.tipo_documento_id = %s and t.numero_documento = ?
				""".formatted(TIPO_DOCUMENTO), new CoincidenciaMapper(), tipoDocumento, numero);
		return coincidencias.isEmpty() ? null : coincidencias.get(0);
	}

	private String estadoDatos(DatosTercero d) {
		return d.numeroDocumento() == null ? "PENDIENTE" : "COMPLETO";
	}

	private static final class TerceroMapper implements RowMapper<Tercero> {

		@Override
		public Tercero mapRow(ResultSet rs, int fila) throws SQLException {
			CondicionesCliente cliente = null;
			if (rs.getBoolean("es_cliente")) {
				cliente = new CondicionesCliente(rs.getInt("plazo_cliente"), rs.getBigDecimal("cupo_credito"),
						rs.getString("control_credito"));
			}
			CondicionesProveedor proveedor = null;
			if (rs.getBoolean("es_proveedor")) {
				proveedor = new CondicionesProveedor(rs.getInt("plazo_proveedor"), rs.getString("banco"),
						rs.getString("tipo_cuenta"), rs.getString("numero_cuenta"));
			}
			return new Tercero(rs.getString("codigo"), rs.getString("tipo_persona"), rs.getString("tipo_documento"),
					rs.getString("numero_documento"), rs.getString("dv"), rs.getString("razon_social"),
					rs.getString("nombres"), rs.getString("apellidos"), rs.getString("nombre_comercial"),
					rs.getString("nombre_completo"), rs.getBoolean("responsable_iva"), rs.getString("direccion"),
					rs.getString("codigo_dane"), rs.getString("municipio"), rs.getString("correo"),
					rs.getString("telefono"), rs.getString("notas"), rs.getBoolean("activo"),
					rs.getString("estado_datos"), cliente, proveedor, dinero(rs, "debe"), dinero(rs, "saldo_favor"),
					dinero(rs, "le_debemos"));
		}

		private BigDecimal dinero(ResultSet rs, String columna) throws SQLException {
			return rs.getBigDecimal(columna);
		}

	}

	private static final class CoincidenciaMapper implements RowMapper<Coincidencia> {

		@Override
		public Coincidencia mapRow(ResultSet rs, int fila) throws SQLException {
			return new Coincidencia(rs.getString("codigo"), rs.getString("nombre_completo"),
					rs.getBoolean("es_cliente"), rs.getBoolean("es_proveedor"));
		}

	}

}
