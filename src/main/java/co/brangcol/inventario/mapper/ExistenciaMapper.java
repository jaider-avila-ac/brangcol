package co.brangcol.inventario.mapper;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.ExistenciaResponse;
import co.brangcol.inventario.dto.ResumenInventarioResponse;
import co.brangcol.inventario.model.Existencia;
import co.brangcol.inventario.model.FiltroExistencias;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.ResumenInventario;
import co.brangcol.shared.web.PageResponse;

@Component
public class ExistenciaMapper {

	public PageResponse<ExistenciaResponse> aPagina(Pagina<Existencia> pagina, FiltroExistencias filtro) {
		List<ExistenciaResponse> contenido = new ArrayList<>();
		for (Existencia e : pagina.items()) {
			contenido.add(new ExistenciaResponse(e.item(), e.sku(), e.nombre(), e.unidad(), e.permiteFraccion(),
					e.cantidad(), e.minimo(), e.costoPromedio(),
					e.cantidad().multiply(e.costoPromedio()).setScale(2, RoundingMode.HALF_UP)));
		}
		return Paginas.de(contenido, pagina.total(), filtro.pagina(), filtro.tamano());
	}

	public ResumenInventarioResponse aRespuesta(ResumenInventario resumen) {
		return new ResumenInventarioResponse(resumen.productos(), resumen.valorTotal(), resumen.bajoMinimo());
	}

}
