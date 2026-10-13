package co.brangcol.producto.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.producto.model.ComponenteReceta;
import co.brangcol.producto.model.Receta;
import co.brangcol.producto.repository.RecetaRepository;
import co.brangcol.producto.service.RecetaService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class RecetaServiceImpl implements RecetaService {

	private static final Pattern CODIGO = Pattern.compile("^[A-Za-z0-9]{12,16}$");

	private static final Pattern UNIDAD = Pattern.compile("^[A-Za-z0-9]{1,4}$");

	private static final BigDecimal CIEN = BigDecimal.valueOf(100);

	private final RecetaRepository recetaRepository;

	private final VerificadorPermisos permisos;

	public RecetaServiceImpl(RecetaRepository recetaRepository, VerificadorPermisos permisos) {
		this.recetaRepository = recetaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public Receta obtener(String itemCodigo) {
		permisos.exigir("productos:item:consultar");
		return recetaRepository.vigente(itemCodigo);
	}

	@Override
	@Transactional
	public Receta guardar(String itemCodigo, List<ComponenteReceta> componentes) {
		recetaRepository.guardar(itemCodigo, aJson(componentes));
		return recetaRepository.vigente(itemCodigo);
	}

	private String aJson(List<ComponenteReceta> componentes) {
		if (componentes == null || componentes.isEmpty()) {
			fallar("Agregue al menos un insumo o componente");
		}
		Set<String> vistos = new HashSet<>();
		StringBuilder json = new StringBuilder("[");
		for (ComponenteReceta c : componentes) {
			if (c.item() == null || !CODIGO.matcher(c.item()).matches()) {
				fallar("Uno de los componentes no es válido");
			}
			if (!vistos.add(c.item())) {
				fallar("Un componente está repetido en la receta");
			}
			if (c.cantidad() == null || c.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
				fallar("La cantidad de cada componente debe ser mayor que cero");
			}
			BigDecimal merma = c.merma() == null ? BigDecimal.ZERO : c.merma();
			if (merma.compareTo(BigDecimal.ZERO) < 0 || merma.compareTo(CIEN) > 0) {
				fallar("La merma debe estar entre 0 y 100 %");
			}
			if (json.length() > 1) {
				json.append(',');
			}
			if (c.presentacion() != null && !UNIDAD.matcher(c.presentacion()).matches()) {
				fallar("La presentación de un componente no es válida");
			}
			json.append("{\"item\":\"").append(c.item()).append("\",\"cantidad\":")
				.append(c.cantidad().toPlainString()).append(",\"merma\":").append(merma.toPlainString());
			if (c.presentacion() != null) {
				json.append(",\"presentacion\":\"").append(c.presentacion()).append('"');
			}
			json.append('}');
		}
		return json.append(']').toString();
	}

	private void fallar(String mensaje) {
		throw new NegocioException(HttpStatus.BAD_REQUEST, mensaje);
	}

}
