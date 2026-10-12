package co.brangcol.usuario.service;

import java.util.List;

import co.brangcol.usuario.model.Rol;

public interface RolService {

	List<Rol> listar();

	void validarActivo(String codigo);

}
