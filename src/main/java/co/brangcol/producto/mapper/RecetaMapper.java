package co.brangcol.producto.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.producto.dto.ComponenteDto;
import co.brangcol.producto.dto.RecetaRequest;
import co.brangcol.producto.dto.RecetaResponse;
import co.brangcol.producto.model.ComponenteReceta;
import co.brangcol.producto.model.Receta;

@Component
public class RecetaMapper {

	private static final BigDecimal CIEN = BigDecimal.valueOf(100);

	public List<ComponenteReceta> aComponentes(RecetaRequest request) {
		List<ComponenteReceta> componentes = new ArrayList<>();
		if (request == null || request.componentes() == null) {
			return componentes;
		}
		for (ComponenteDto c : request.componentes()) {
			componentes.add(new ComponenteReceta(c.item(), null, null, c.cantidad(), c.merma(), null));
		}
		return componentes;
	}

	// El costo de cada componente incluye su merma: cantidad x (1 + merma / 100) x costo promedio
	public RecetaResponse aRespuesta(Receta receta) {
		if (receta == null) {
			return new RecetaResponse(0, false, new ArrayList<>(), BigDecimal.ZERO);
		}
		List<ComponenteDto> componentes = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO;
		for (ComponenteReceta c : receta.componentes()) {
			componentes.add(new ComponenteDto(c.item(), c.nombre(), c.unidadSimbolo(), c.cantidad(), c.merma(),
					c.costoUnitario()));
			BigDecimal factor = BigDecimal.ONE.add(c.merma().divide(CIEN, 6, RoundingMode.HALF_UP));
			total = total.add(c.cantidad().multiply(factor).multiply(c.costoUnitario()));
		}
		return new RecetaResponse(receta.version(), receta.bloqueada(), componentes,
				total.setScale(2, RoundingMode.HALF_UP));
	}

}
