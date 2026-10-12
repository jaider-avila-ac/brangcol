package co.brangcol.usuario.mapper;

import org.springframework.stereotype.Component;

import co.brangcol.usuario.dto.AceptarInvitacionRequest;
import co.brangcol.usuario.dto.InvitacionPublicaResponse;
import co.brangcol.usuario.model.Aceptacion;
import co.brangcol.usuario.model.InvitacionPublica;

@Component
public class InvitacionPublicaMapper {

	public InvitacionPublicaResponse aRespuesta(InvitacionPublica invitacion) {
		return new InvitacionPublicaResponse(invitacion.empresa(), invitacion.correo(), invitacion.rol(),
				invitacion.tieneCuenta(), invitacion.venceEn());
	}

	public Aceptacion aAceptacion(AceptarInvitacionRequest request) {
		return new Aceptacion(request.token(), request.nombres(), request.apellidos(), request.contrasena());
	}

}
