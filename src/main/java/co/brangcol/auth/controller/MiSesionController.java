package co.brangcol.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.auth.dto.SesionResponse;
import co.brangcol.auth.dto.YoResponse;
import co.brangcol.auth.mapper.SesionMapper;
import co.brangcol.auth.service.MiSesionService;

@RestController
@RequestMapping("/api/v1/auth")
public class MiSesionController {

	private final MiSesionService miSesionService;

	private final SesionMapper sesionMapper;

	public MiSesionController(MiSesionService miSesionService, SesionMapper sesionMapper) {
		this.miSesionService = miSesionService;
		this.sesionMapper = sesionMapper;
	}

	@GetMapping("/yo")
	public YoResponse yo() {
		return sesionMapper.aYo(miSesionService.miSesion(), miSesionService.imagenes());
	}

	@GetMapping("/sesiones")
	public List<SesionResponse> sesiones() {
		return sesionMapper.aSesiones(miSesionService.misSesiones());
	}

	@DeleteMapping("/sesiones/{codigo}")
	public ResponseEntity<Void> cerrarSesion(@PathVariable String codigo) {
		miSesionService.cerrarSesionAbierta(codigo);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/sesiones")
	public ResponseEntity<Void> cerrarOtrasSesiones() {
		miSesionService.cerrarOtrasSesiones();
		return ResponseEntity.noContent().build();
	}

}
