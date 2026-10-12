package co.brangcol.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.dto.ImagenResponse;
import co.brangcol.auth.service.MiFotoService;

@RestController
@RequestMapping("/api/v1/auth/foto")
public class MiFotoController {

	private final MiFotoService miFotoService;

	public MiFotoController(MiFotoService miFotoService) {
		this.miFotoService = miFotoService;
	}

	@PostMapping
	public ImagenResponse subir(@RequestParam("imagen") MultipartFile imagen) {
		return new ImagenResponse(miFotoService.subir(imagen));
	}

}
