package co.brangcol.producto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.producto.dto.CategoriaRequest;
import co.brangcol.producto.dto.CategoriaResponse;
import co.brangcol.producto.mapper.CategoriaMapper;
import co.brangcol.producto.service.CategoriaService;
import co.brangcol.shared.texto.Textos;

@RestController
@RequestMapping("/api/v1/productos/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	private final CategoriaMapper categoriaMapper;

	public CategoriaController(CategoriaService categoriaService, CategoriaMapper categoriaMapper) {
		this.categoriaService = categoriaService;
		this.categoriaMapper = categoriaMapper;
	}

	@GetMapping
	public List<CategoriaResponse> listar() {
		return categoriaMapper.aRespuestas(categoriaService.listar());
	}

	@PostMapping
	public List<CategoriaResponse> crear(@RequestBody CategoriaRequest request) {
		return categoriaMapper.aRespuestas(
				categoriaService.crear(Textos.vacioANulo(request.nombre()), Textos.vacioANulo(request.padre())));
	}

	@PutMapping("/{codigo}")
	public List<CategoriaResponse> actualizar(@PathVariable String codigo, @RequestBody CategoriaRequest request) {
		return categoriaMapper.aRespuestas(categoriaService.actualizar(codigo, Textos.vacioANulo(request.nombre()),
				Textos.vacioANulo(request.padre()), !Boolean.FALSE.equals(request.activa())));
	}

}
