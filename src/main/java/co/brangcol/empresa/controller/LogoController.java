package co.brangcol.empresa.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.dto.ImagenResponse;
import co.brangcol.empresa.service.LogoService;

@RestController
@RequestMapping("/api/v1/empresa/logo")
public class LogoController {

	private final LogoService logoService;

	public LogoController(LogoService logoService) {
		this.logoService = logoService;
	}

	@PostMapping
	public ImagenResponse subir(@RequestParam("imagen") MultipartFile imagen) {
		return new ImagenResponse(logoService.subir(imagen));
	}

}
