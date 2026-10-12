package co.brangcol.usuario.service;

import java.util.List;

import co.brangcol.usuario.model.Usuario;

public interface UsuarioService {

	List<Usuario> listar();

	Usuario cambiarRol(String codigo, String rolCodigo);

	Usuario cambiarEstado(String codigo, boolean activo);

}
