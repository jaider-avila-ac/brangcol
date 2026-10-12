package co.brangcol.producto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.producto.dto.CategoriaResponse;
import co.brangcol.producto.model.Categoria;

@Component
public class CategoriaMapper {

	public List<CategoriaResponse> aRespuestas(List<Categoria> categorias) {
		List<CategoriaResponse> respuesta = new ArrayList<>();
		for (Categoria c : categorias) {
			respuesta.add(new CategoriaResponse(c.codigo(), c.nombre(), c.padreCodigo(), c.padreNombre(), c.activa(),
					c.productos()));
		}
		return respuesta;
	}

}
