package co.brangcol.usuario.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.usuario.model.Rol;
import co.brangcol.usuario.repository.RolRepository;
import co.brangcol.usuario.service.RolService;

@Service
public class RolServiceImpl implements RolService {

	private final RolRepository rolRepository;

	private final VerificadorPermisos permisos;

	public RolServiceImpl(RolRepository rolRepository, VerificadorPermisos permisos) {
		this.rolRepository = rolRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Rol> listar() {
		permisos.exigir("plataforma:usuario:consultar");
		return rolRepository.listar();
	}

	@Override
	@Transactional(readOnly = true)
	public void validarActivo(String codigo) {
		if (!rolRepository.existeActivo(codigo)) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "El rol no existe");
		}
	}

}
