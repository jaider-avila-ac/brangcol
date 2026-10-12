package co.brangcol.usuario.service;

import co.brangcol.usuario.model.Aceptacion;
import co.brangcol.usuario.model.InvitacionPublica;

public interface InvitacionPublicaService {

	InvitacionPublica consultar(String token);

	void aceptar(Aceptacion aceptacion);

}
