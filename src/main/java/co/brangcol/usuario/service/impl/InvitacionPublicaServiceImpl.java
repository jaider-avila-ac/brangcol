package co.brangcol.usuario.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.usuario.model.Aceptacion;
import co.brangcol.usuario.model.InvitacionPublica;
import co.brangcol.usuario.repository.InvitacionPublicaRepository;
import co.brangcol.usuario.service.InvitacionPublicaService;

@Service
public class InvitacionPublicaServiceImpl implements InvitacionPublicaService {

	private final InvitacionPublicaRepository invitacionPublicaRepository;

	public InvitacionPublicaServiceImpl(InvitacionPublicaRepository invitacionPublicaRepository) {
		this.invitacionPublicaRepository = invitacionPublicaRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public InvitacionPublica consultar(String token) {
		return invitacionPublicaRepository.consultar(token);
	}

	@Override
	@Transactional
	public void aceptar(Aceptacion aceptacion) {
		invitacionPublicaRepository.aceptar(aceptacion);
	}

}
