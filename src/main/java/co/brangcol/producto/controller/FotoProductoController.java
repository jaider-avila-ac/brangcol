package co.brangcol.producto.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.dto.ImagenResponse;
import co.brangcol.producto.service.FotoProductoService;

@RestController
@RequestMapping("/api/v1/productos/{codigo}/foto")
public class FotoProductoController {

	private final FotoProductoService fotoProductoService;

	public FotoProductoController(FotoProductoService fotoProductoService) {
		this.fotoProductoService = fotoProductoService;
	}

	@PostMapping
	public ImagenResponse subir(@PathVariable String codigo, @RequestParam("imagen") MultipartFile imagen) {
		return new ImagenResponse(fotoProductoService.subir(codigo, imagen));
	}

}
