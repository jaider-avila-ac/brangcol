package co.brangcol.usuario.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.usuario.model.Usuario;
import co.brangcol.usuario.repository.UsuarioRepository;
import co.brangcol.usuario.service.RolService;
import co.brangcol.usuario.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private final UsuarioRepository usuarioRepository;

	private final RolService rolService;

	private final VerificadorPermisos permisos;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolService rolService,
			VerificadorPermisos permisos) {
		this.usuarioRepository = usuarioRepository;
		this.rolService = rolService;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		permisos.exigir("plataforma:usuario:consultar");
		return usuarioRepository.listar();
	}

	@Override
	@Transactional
	public Usuario cambiarRol(String codigo, String rolCodigo) {
		rolService.validarActivo(rolCodigo);
		if (usuarioRepository.cambiarRol(codigo, rolCodigo) == 0) {
			throw noEncontrado();
		}
		return usuarioRepository.obtener(codigo);
	}

	@Override
	@Transactional
	public Usuario cambiarEstado(String codigo, boolean activo) {
		if (usuarioRepository.cambiarEstado(codigo, activo) == 0) {
			throw noEncontrado();
		}
		return usuarioRepository.obtener(codigo);
	}

	private NegocioException noEncontrado() {
		return new NegocioException(HttpStatus.NOT_FOUND, "El usuario no existe");
	}

}
