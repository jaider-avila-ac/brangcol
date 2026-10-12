package co.brangcol.producto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.producto.dto.ItemResumenResponse;
import co.brangcol.producto.model.FiltroItems;
import co.brangcol.producto.model.ItemResumen;
import co.brangcol.producto.model.PaginaItems;
import co.brangcol.shared.web.PageResponse;

@Component
public class ItemResumenMapper {

	public PageResponse<ItemResumenResponse> aPagina(PaginaItems pagina, FiltroItems filtro) {
		List<ItemResumenResponse> contenido = new ArrayList<>();
		for (ItemResumen i : pagina.items()) {
			contenido.add(new ItemResumenResponse(i.codigo(), i.sku(), i.nombre(), i.tipo(), i.categoria(),
					i.unidadSimbolo(), i.precioVenta(), i.precioConImpuesto(), i.costoPromedio(), i.activo(),
					i.foto()));
		}
		int paginas = (int) ((pagina.total() + filtro.tamano() - 1) / filtro.tamano());
		return new PageResponse<>(contenido, filtro.pagina(), filtro.tamano(), pagina.total(), paginas);
	}

}
