package co.brangcol.usuario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.usuario.dto.InvitacionCreadaResponse;
import co.brangcol.usuario.dto.InvitacionResponse;
import co.brangcol.usuario.model.InvitacionCreada;
import co.brangcol.usuario.model.InvitacionPendiente;

@Component
public class InvitacionMapper {

	public List<InvitacionResponse> aRespuestas(List<InvitacionPendiente> invitaciones) {
		List<InvitacionResponse> respuesta = new ArrayList<>();
		for (InvitacionPendiente invitacion : invitaciones) {
			respuesta.add(new InvitacionResponse(invitacion.codigo(), invitacion.correo(), invitacion.rol(),
					invitacion.venceEn(), invitacion.creadaEn()));
		}
		return respuesta;
	}

	public InvitacionCreadaResponse aCreada(InvitacionCreada invitacion, String enlace, boolean correoEnviado) {
		return new InvitacionCreadaResponse(invitacion.codigo(), invitacion.correo(), enlace, invitacion.venceEn(),
				correoEnviado);
	}

}
