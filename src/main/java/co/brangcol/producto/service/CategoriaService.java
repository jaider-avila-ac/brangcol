package co.brangcol.producto.service;

import java.util.List;

import co.brangcol.producto.model.Categoria;

public interface CategoriaService {

	List<Categoria> listar();

	List<Categoria> crear(String nombre, String padre);

	List<Categoria> actualizar(String codigo, String nombre, String padre, boolean activa);

	void validar(String codigo);

}
