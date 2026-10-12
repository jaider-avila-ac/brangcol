package co.brangcol.usuario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.usuario.dto.CambiarEstadoRequest;
import co.brangcol.usuario.dto.CambiarRolRequest;
import co.brangcol.usuario.dto.UsuarioResponse;
import co.brangcol.usuario.mapper.UsuarioMapper;
import co.brangcol.usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	private final UsuarioMapper usuarioMapper;

	public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
		this.usuarioService = usuarioService;
		this.usuarioMapper = usuarioMapper;
	}

	@GetMapping
	public List<UsuarioResponse> listar() {
		return usuarioMapper.aRespuestas(usuarioService.listar());
	}

	@PutMapping("/{codigo}/rol")
	public UsuarioResponse cambiarRol(@PathVariable String codigo, @Valid @RequestBody CambiarRolRequest request) {
		return usuarioMapper.aRespuesta(usuarioService.cambiarRol(codigo, request.rol()));
	}

	@PutMapping("/{codigo}/estado")
	public UsuarioResponse cambiarEstado(@PathVariable String codigo, @RequestBody CambiarEstadoRequest request) {
		return usuarioMapper.aRespuesta(usuarioService.cambiarEstado(codigo, request.activo()));
	}

}
