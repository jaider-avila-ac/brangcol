package co.brangcol.producto.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.producto.model.Marca;
import co.brangcol.producto.repository.MarcaRepository;
import co.brangcol.producto.service.MarcaService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class MarcaServiceImpl implements MarcaService {

	private final MarcaRepository marcaRepository;

	private final VerificadorPermisos permisos;

	public MarcaServiceImpl(MarcaRepository marcaRepository, VerificadorPermisos permisos) {
		this.marcaRepository = marcaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Marca> listar() {
		permisos.exigir("productos:item:consultar");
		return marcaRepository.listar();
	}

	@Override
	@Transactional
	public List<Marca> crear(String nombre) {
		validarNombre(nombre);
		marcaRepository.crear(nombre);
		return marcaRepository.listar();
	}

	@Override
	@Transactional
	public List<Marca> actualizar(String codigo, String nombre, boolean activa) {
		validarNombre(nombre);
		if (marcaRepository.actualizar(codigo, nombre, activa) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "La marca no existe");
		}
		return marcaRepository.listar();
	}

	@Override
	@Transactional(readOnly = true)
	public void validar(String codigo) {
		if (!marcaRepository.existe(codigo)) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "La marca no existe");
		}
	}

	private void validarNombre(String nombre) {
		if (nombre == null || nombre.length() > 80) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Escriba un nombre de hasta 80 caracteres");
		}
	}

}
