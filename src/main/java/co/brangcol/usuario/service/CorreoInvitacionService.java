package co.brangcol.usuario.service;

import co.brangcol.usuario.model.InvitacionCreada;

public interface CorreoInvitacionService {

	boolean enviar(InvitacionCreada invitacion, String enlace);

}
