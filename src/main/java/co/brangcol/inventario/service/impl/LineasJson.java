package co.brangcol.inventario.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.brangcol.inventario.model.LineaInventario;
import co.brangcol.shared.error.NegocioException;

// Valida las lineas de un documento de inventario y las convierte al JSON que esperan las funciones de la base
@Component
public class LineasJson {

	private static final Pattern CODIGO = Pattern.compile("^[A-Za-z0-9]{12,16}$");

	public String conSentido(List<LineaInventario> lineas) {
		StringBuilder json = new StringBuilder("[");
		for (LineaInventario l : validar(lineas, false)) {
			if (!"ENTRADA".equals(l.sentido()) && !"SALIDA".equals(l.sentido())) {
				fallar("Indique si cada producto entra o sale");
			}
			if (l.costo() != null && l.costo().compareTo(BigDecimal.ZERO) < 0) {
				fallar("El costo no puede ser negativo");
			}
			separar(json);
			json.append("{\"item\":\"").append(l.item()).append("\",\"sentido\":\"").append(l.sentido())
				.append("\",\"cantidad\":").append(l.cantidad().toPlainString());
			if (l.costo() != null && "ENTRADA".equals(l.sentido())) {
				json.append(",\"costo\":").append(l.costo().toPlainString());
			}
			json.append('}');
		}
		return json.append(']').toString();
	}

	// En conteos y recepciones la cantidad puede ser cero
	public String cantidades(List<LineaInventario> lineas, boolean permiteCero) {
		StringBuilder json = new StringBuilder("[");
		for (LineaInventario l : validar(lineas, permiteCero)) {
			separar(json);
			json.append("{\"item\":\"").append(l.item()).append("\",\"cantidad\":")
				.append(l.cantidad().toPlainString()).append('}');
		}
		return json.append(']').toString();
	}

	private List<LineaInventario> validar(List<LineaInventario> lineas, boolean permiteCero) {
		if (lineas == null || lineas.isEmpty()) {
			fallar("Agregue al menos un producto");
		}
		Set<String> vistos = new HashSet<>();
		for (LineaInventario l : lineas) {
			if (l.item() == null || !CODIGO.matcher(l.item()).matches()) {
				fallar("Uno de los productos no es válido");
			}
			if (!vistos.add(l.item())) {
				fallar("Un producto aparece dos veces");
			}
			if (l.cantidad() == null) {
				fallar("Escriba la cantidad de cada producto");
			}
			int signo = l.cantidad().compareTo(BigDecimal.ZERO);
			if (signo < 0 || (signo == 0 && !permiteCero)) {
				fallar("La cantidad de cada producto debe ser mayor que cero");
			}
		}
		return lineas;
	}

	private void separar(StringBuilder json) {
		if (json.length() > 1) {
			json.append(',');
		}
	}

	private void fallar(String mensaje) {
		throw new NegocioException(HttpStatus.BAD_REQUEST, mensaje);
	}

}
