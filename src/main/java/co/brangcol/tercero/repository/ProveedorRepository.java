package co.brangcol.tercero.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.tercero.model.CondicionesProveedor;

@Repository
public class ProveedorRepository {

	private static final String VALORES = """
			select t.id, ?, (select id from parametro.banco where nombre = ?), ?, ?
			from tercero.tercero t where t.codigo = ?
			""";

	private final JdbcTemplate jdbc;

	public ProveedorRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void guardar(String codigoTercero, CondicionesProveedor condiciones) {
		jdbc.update("insert into tercero.proveedor (tercero_id, plazo_dias, banco_id, numero_cuenta, tipo_cuenta) " + VALORES
				+ """
						on conflict (empresa_id, tercero_id) do update
						set plazo_dias = excluded.plazo_dias, banco_id = excluded.banco_id, numero_cuenta = excluded.numero_cuenta,
						    tipo_cuenta = excluded.tipo_cuenta
						""", condiciones.plazoDias(), condiciones.banco(), condiciones.numeroCuenta(),
				condiciones.tipoCuenta(), codigoTercero);
	}

	public int agregarSiNoExiste(String codigoTercero, CondicionesProveedor condiciones) {
		return jdbc.update("insert into tercero.proveedor (tercero_id, plazo_dias, banco_id, numero_cuenta, tipo_cuenta) "
				+ VALORES + " on conflict (empresa_id, tercero_id) do nothing", condiciones.plazoDias(),
				condiciones.banco(), condiciones.numeroCuenta(), condiciones.tipoCuenta(), codigoTercero);
	}

}
