package co.brangcol.producto.service;

import java.util.List;

import co.brangcol.producto.model.Marca;

public interface MarcaService {

	List<Marca> listar();

	List<Marca> crear(String nombre);

	List<Marca> actualizar(String codigo, String nombre, boolean activa);

	void validar(String codigo);

}
