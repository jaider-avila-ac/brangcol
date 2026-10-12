package co.brangcol.auth.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;
import co.brangcol.auth.repository.MiSesionRepository;
import co.brangcol.auth.service.MiSesionService;
import co.brangcol.shared.error.NegocioException;

@Service
public class MiSesionServiceImpl implements MiSesionService {

	private final MiSesionRepository miSesionRepository;

	public MiSesionServiceImpl(MiSesionRepository miSesionRepository) {
		this.miSesionRepository = miSesionRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public SesionUsuario miSesion() {
		return miSesionRepository.miSesion();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SesionAbierta> misSesiones() {
		return miSesionRepository.misSesiones();
	}

	@Override
	@Transactional
	public void cerrarSesionAbierta(String codigo) {
		if (miSesionRepository.cerrarMisSesiones(codigo) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "La sesión no existe o ya fue cerrada");
		}
	}

	@Override
	@Transactional
	public void cerrarOtrasSesiones() {
		miSesionRepository.cerrarMisSesiones(null);
	}

}
