package co.brangcol.shared.error;

import java.sql.SQLException;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TraductorErrorBaseDatos {

	private static final String PREFIJO_NEGOCIO = "BC";

	public ErrorTraducido traducir(Throwable error) {
		SQLException sql = buscarSqlException(error);
		if (sql == null || sql.getSQLState() == null) {
			return null;
		}
		String codigo = sql.getSQLState();
		if (codigo.startsWith(PREFIJO_NEGOCIO)) {
			return new ErrorTraducido(EstadoPorCodigo.de(codigo), mensajeServidor(sql));
		}
		if ("23505".equals(codigo)) {
			return new ErrorTraducido(HttpStatus.CONFLICT, "Ya existe un registro con esos datos");
		}
		if ("23503".equals(codigo)) {
			return new ErrorTraducido(HttpStatus.CONFLICT,
					"El registro está en uso o hace referencia a datos que no existen");
		}
		if (codigo.startsWith("22") || codigo.startsWith("23")) {
			return new ErrorTraducido(HttpStatus.BAD_REQUEST, "Hay datos inválidos o incompletos");
		}
		return null;
	}

	private SQLException buscarSqlException(Throwable error) {
		Throwable actual = error;
		int vueltas = 0;
		while (actual != null && vueltas < 20) {
			if (actual instanceof SQLException sql) {
				return sql;
			}
			actual = actual.getCause();
			vueltas++;
		}
		return null;
	}

	private String mensajeServidor(SQLException sql) {
		if (sql instanceof PSQLException psql) {
			ServerErrorMessage servidor = psql.getServerErrorMessage();
			if (servidor != null && servidor.getMessage() != null) {
				return servidor.getMessage();
			}
		}
		return sql.getMessage();
	}

}
