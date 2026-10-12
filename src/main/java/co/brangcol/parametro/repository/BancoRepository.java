package co.brangcol.parametro.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BancoRepository {

	private final JdbcTemplate jdbc;

	public BancoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<String> nombres() {
		return jdbc.queryForList("select nombre from parametro.banco order by nombre", String.class);
	}

}
