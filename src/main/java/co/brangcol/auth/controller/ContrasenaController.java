package co.brangcol.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.auth.dto.CambiarContrasenaRequest;
import co.brangcol.auth.service.ContrasenaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth/contrasena")
public class ContrasenaController {

	private final ContrasenaService contrasenaService;

	public ContrasenaController(ContrasenaService contrasenaService) {
		this.contrasenaService = contrasenaService;
	}

	@PutMapping
	public ResponseEntity<Void> cambiar(@Valid @RequestBody CambiarContrasenaRequest request) {
		contrasenaService.cambiar(request.actual(), request.nueva());
		return ResponseEntity.noContent().build();
	}

}
