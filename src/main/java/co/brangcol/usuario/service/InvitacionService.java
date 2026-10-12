package co.brangcol.usuario.service;

import java.util.List;

import co.brangcol.usuario.model.InvitacionCreada;
import co.brangcol.usuario.model.InvitacionPendiente;

public interface InvitacionService {

	InvitacionCreada crear(String correo, String rolCodigo);

	List<InvitacionPendiente> pendientes();

	void revocar(String codigo);

}
