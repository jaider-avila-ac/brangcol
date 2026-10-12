package co.brangcol.usuario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.usuario.dto.AceptarInvitacionRequest;
import co.brangcol.usuario.dto.ConsultarInvitacionRequest;
import co.brangcol.usuario.dto.InvitacionPublicaResponse;
import co.brangcol.usuario.mapper.InvitacionPublicaMapper;
import co.brangcol.usuario.service.InvitacionPublicaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invitaciones")
public class InvitacionPublicaController {

	private final InvitacionPublicaService invitacionPublicaService;

	private final InvitacionPublicaMapper invitacionPublicaMapper;

	public InvitacionPublicaController(InvitacionPublicaService invitacionPublicaService,
			InvitacionPublicaMapper invitacionPublicaMapper) {
		this.invitacionPublicaService = invitacionPublicaService;
		this.invitacionPublicaMapper = invitacionPublicaMapper;
	}

	@PostMapping("/consultar")
	public InvitacionPublicaResponse consultar(@Valid @RequestBody ConsultarInvitacionRequest request) {
		return invitacionPublicaMapper.aRespuesta(invitacionPublicaService.consultar(request.token()));
	}

	@PostMapping("/aceptar")
	public ResponseEntity<Void> aceptar(@Valid @RequestBody AceptarInvitacionRequest request) {
		invitacionPublicaService.aceptar(invitacionPublicaMapper.aAceptacion(request));
		return ResponseEntity.noContent().build();
	}

}
