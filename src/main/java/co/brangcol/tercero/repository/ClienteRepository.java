package co.brangcol.tercero.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.brangcol.tercero.model.CondicionesCliente;

@Repository
public class ClienteRepository {

	private final JdbcTemplate jdbc;

	public ClienteRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void guardar(String codigoTercero, CondicionesCliente condiciones) {
		jdbc.update("""
				insert into tercero.cliente (tercero_id, plazo_dias, cupo_credito, control_credito)
				select id, ?, ?, ? from tercero.tercero where codigo = ?
				on conflict (empresa_id, tercero_id) do update
				set plazo_dias = excluded.plazo_dias, cupo_credito = excluded.cupo_credito, control_credito = excluded.control_credito
				""", condiciones.plazoDias(), condiciones.cupoCredito(), condiciones.controlCredito(), codigoTercero);
	}

	// Usado al importar: si ya es cliente se conservan sus condiciones
	public int agregarSiNoExiste(String codigoTercero, CondicionesCliente condiciones) {
		return jdbc.update("""
				insert into tercero.cliente (tercero_id, plazo_dias, cupo_credito, control_credito)
				select id, ?, ?, ? from tercero.tercero where codigo = ?
				on conflict (empresa_id, tercero_id) do nothing
				""", condiciones.plazoDias(), condiciones.cupoCredito(), condiciones.controlCredito(), codigoTercero);
	}

}
