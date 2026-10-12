package co.brangcol.usuario.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.usuario.model.InvitacionCreada;
import co.brangcol.usuario.model.InvitacionPendiente;
import co.brangcol.usuario.repository.InvitacionRepository;
import co.brangcol.usuario.service.InvitacionService;

@Service
public class InvitacionServiceImpl implements InvitacionService {

	private final InvitacionRepository invitacionRepository;

	private final VerificadorPermisos permisos;

	public InvitacionServiceImpl(InvitacionRepository invitacionRepository, VerificadorPermisos permisos) {
		this.invitacionRepository = invitacionRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public InvitacionCreada crear(String correo, String rolCodigo) {
		return invitacionRepository.crear(correo, rolCodigo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InvitacionPendiente> pendientes() {
		permisos.exigir("plataforma:usuario:consultar");
		return invitacionRepository.pendientes();
	}

	@Override
	@Transactional
	public void revocar(String codigo) {
		invitacionRepository.revocar(codigo);
	}

}
