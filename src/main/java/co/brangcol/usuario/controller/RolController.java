package co.brangcol.usuario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.usuario.dto.RolResponse;
import co.brangcol.usuario.mapper.RolMapper;
import co.brangcol.usuario.service.RolService;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

	private final RolService rolService;

	private final RolMapper rolMapper;

	public RolController(RolService rolService, RolMapper rolMapper) {
		this.rolService = rolService;
		this.rolMapper = rolMapper;
	}

	@GetMapping
	public List<RolResponse> listar() {
		return rolMapper.aRespuestas(rolService.listar());
	}

}
