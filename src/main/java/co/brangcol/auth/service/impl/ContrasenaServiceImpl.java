package co.brangcol.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.auth.repository.ContrasenaRepository;
import co.brangcol.auth.service.ContrasenaService;

@Service
public class ContrasenaServiceImpl implements ContrasenaService {

	private final ContrasenaRepository contrasenaRepository;

	public ContrasenaServiceImpl(ContrasenaRepository contrasenaRepository) {
		this.contrasenaRepository = contrasenaRepository;
	}

	@Override
	@Transactional
	public void cambiar(String actual, String nueva) {
		contrasenaRepository.cambiarMiContrasena(actual, nueva);
	}

}
