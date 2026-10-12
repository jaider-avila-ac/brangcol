package co.brangcol.producto.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.producto.model.Categoria;
import co.brangcol.producto.repository.CategoriaRepository;
import co.brangcol.producto.service.CategoriaService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class CategoriaServiceImpl implements CategoriaService {

	private final CategoriaRepository categoriaRepository;

	private final VerificadorPermisos permisos;

	public CategoriaServiceImpl(CategoriaRepository categoriaRepository, VerificadorPermisos permisos) {
		this.categoriaRepository = categoriaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Categoria> listar() {
		permisos.exigir("productos:item:consultar");
		return categoriaRepository.listar();
	}

	@Override
	@Transactional
	public List<Categoria> crear(String nombre, String padre) {
		validarNombre(nombre);
		if (padre != null) {
			validar(padre);
		}
		categoriaRepository.crear(nombre, padre);
		return categoriaRepository.listar();
	}

	@Override
	@Transactional
	public List<Categoria> actualizar(String codigo, String nombre, String padre, boolean activa) {
		validarNombre(nombre);
		if (padre != null) {
			validar(padre);
		}
		if (categoriaRepository.actualizar(codigo, nombre, padre, activa) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "La categoría no existe");
		}
		return categoriaRepository.listar();
	}

	@Override
	@Transactional(readOnly = true)
	public void validar(String codigo) {
		if (!categoriaRepository.existe(codigo)) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "La categoría no existe");
		}
	}

	private void validarNombre(String nombre) {
		if (nombre == null || nombre.length() > 80) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Escriba un nombre de hasta 80 caracteres");
		}
	}

}
