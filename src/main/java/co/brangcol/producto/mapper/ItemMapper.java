package co.brangcol.producto.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.producto.dto.ItemRequest;
import co.brangcol.producto.dto.ItemResponse;
import co.brangcol.producto.dto.UnidadAlternaDto;
import co.brangcol.producto.model.DatosItem;
import co.brangcol.producto.model.Item;
import co.brangcol.producto.model.UnidadAlterna;
import co.brangcol.shared.texto.Textos;

@Component
public class ItemMapper {

	public DatosItem aDatos(ItemRequest r) {
		List<String> codigos = new ArrayList<>();
		if (r.codigosBarras() != null) {
			for (String codigo : r.codigosBarras()) {
				String limpio = Textos.vacioANulo(codigo);
				if (limpio != null) {
					codigos.add(limpio);
				}
			}
		}
		List<UnidadAlterna> unidades = new ArrayList<>();
		if (r.unidades() != null) {
			for (UnidadAlternaDto u : r.unidades()) {
				unidades.add(new UnidadAlterna(u.unidad(), null, u.factor(), u.precioVenta(),
						!Boolean.FALSE.equals(u.paraCompra()), !Boolean.FALSE.equals(u.paraVenta())));
			}
		}
		return new DatosItem(r.tipo(), Textos.vacioANulo(r.sku()), Textos.vacioANulo(r.nombre()),
				Textos.vacioANulo(r.descripcion()), Textos.vacioANulo(r.categoria()), Textos.vacioANulo(r.marca()),
				r.unidad(), Boolean.TRUE.equals(r.permiteFraccion()), Textos.vacioANulo(r.impuesto()),
				r.precioVenta() == null ? BigDecimal.ZERO : r.precioVenta(), !Boolean.FALSE.equals(r.seVende()),
				!Boolean.FALSE.equals(r.seCompra()), codigos, unidades);
	}

	public ItemResponse aRespuesta(Item i) {
		List<UnidadAlternaDto> unidades = new ArrayList<>();
		for (UnidadAlterna u : i.unidades()) {
			unidades.add(new UnidadAlternaDto(u.unidad(), u.unidadNombre(), u.factor(), u.precioVenta(),
					u.paraCompra(), u.paraVenta()));
		}
		ItemResponse.Referencia categoria = i.categoria() == null ? null
				: new ItemResponse.Referencia(i.categoria(), i.categoriaNombre());
		ItemResponse.Referencia marca = i.marca() == null ? null
				: new ItemResponse.Referencia(i.marca(), i.marcaNombre());
		return new ItemResponse(i.codigo(), i.sku(), i.nombre(), i.descripcion(), i.tipo(), i.seVende(), i.seCompra(),
				i.manejaInventario(), categoria, marca, i.unidad(), i.unidadSimbolo(), i.permiteFraccion(),
				i.impuesto(), i.tarifaImpuesto(), i.precioVenta(), i.costoPromedio(), i.ultimoCosto(), i.activo(),
				i.foto(), i.codigosBarras(), unidades);
	}

}
