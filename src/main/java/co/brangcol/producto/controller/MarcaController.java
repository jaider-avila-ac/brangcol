package co.brangcol.producto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.producto.dto.MarcaRequest;
import co.brangcol.producto.dto.MarcaResponse;
import co.brangcol.producto.mapper.MarcaMapper;
import co.brangcol.producto.service.MarcaService;
import co.brangcol.shared.texto.Textos;

@RestController
@RequestMapping("/api/v1/productos/marcas")
public class MarcaController {

	private final MarcaService marcaService;

	private final MarcaMapper marcaMapper;

	public MarcaController(MarcaService marcaService, MarcaMapper marcaMapper) {
		this.marcaService = marcaService;
		this.marcaMapper = marcaMapper;
	}

	@GetMapping
	public List<MarcaResponse> listar() {
		return marcaMapper.aRespuestas(marcaService.listar());
	}

	@PostMapping
	public List<MarcaResponse> crear(@RequestBody MarcaRequest request) {
		return marcaMapper.aRespuestas(marcaService.crear(Textos.vacioANulo(request.nombre())));
	}

	@PutMapping("/{codigo}")
	public List<MarcaResponse> actualizar(@PathVariable String codigo, @RequestBody MarcaRequest request) {
		return marcaMapper.aRespuestas(marcaService.actualizar(codigo, Textos.vacioANulo(request.nombre()),
				!Boolean.FALSE.equals(request.activa())));
	}

}
