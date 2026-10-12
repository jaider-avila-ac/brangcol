package co.brangcol.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.shared.web.EnlacesFront;
import co.brangcol.usuario.dto.CrearInvitacionRequest;
import co.brangcol.usuario.dto.InvitacionCreadaResponse;
import co.brangcol.usuario.dto.InvitacionResponse;
import co.brangcol.usuario.mapper.InvitacionMapper;
import co.brangcol.usuario.model.InvitacionCreada;
import co.brangcol.usuario.service.CorreoInvitacionService;
import co.brangcol.usuario.service.InvitacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios/invitaciones")
public class InvitacionController {

	private final InvitacionService invitacionService;

	private final CorreoInvitacionService correoInvitacionService;

	private final EnlacesFront enlaces;

	private final InvitacionMapper invitacionMapper;

	public InvitacionController(InvitacionService invitacionService,
			CorreoInvitacionService correoInvitacionService, EnlacesFront enlaces, InvitacionMapper invitacionMapper) {
		this.invitacionService = invitacionService;
		this.correoInvitacionService = correoInvitacionService;
		this.enlaces = enlaces;
		this.invitacionMapper = invitacionMapper;
	}

	@GetMapping
	public List<InvitacionResponse> pendientes() {
		return invitacionMapper.aRespuestas(invitacionService.pendientes());
	}

	// El correo se envia despues de confirmar la invitacion; si falla, el enlace se puede copiar
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public InvitacionCreadaResponse crear(@Valid @RequestBody CrearInvitacionRequest request) {
		InvitacionCreada invitacion = invitacionService.crear(request.correo(), request.rol());
		String enlace = enlaces.invitacion(invitacion.token());
		boolean enviado = correoInvitacionService.enviar(invitacion, enlace);
		return invitacionMapper.aCreada(invitacion, enlace, enviado);
	}

	@DeleteMapping("/{codigo}")
	public ResponseEntity<Void> revocar(@PathVariable String codigo) {
		invitacionService.revocar(codigo);
		return ResponseEntity.noContent().build();
	}

}
