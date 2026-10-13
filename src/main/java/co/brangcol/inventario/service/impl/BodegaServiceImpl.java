package co.brangcol.inventario.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.Bodega;
import co.brangcol.inventario.model.DatosBodega;
import co.brangcol.inventario.repository.BodegaRepository;
import co.brangcol.inventario.service.BodegaService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class BodegaServiceImpl implements BodegaService {

	private final BodegaRepository bodegaRepository;

	private final VerificadorPermisos permisos;

	public BodegaServiceImpl(BodegaRepository bodegaRepository, VerificadorPermisos permisos) {
		this.bodegaRepository = bodegaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Bodega> listar() {
		permisos.exigir("inventario:existencia:consultar");
		return bodegaRepository.listar();
	}

	@Override
	@Transactional
	public List<Bodega> crear(DatosBodega datos) {
		validarNombre(datos.nombre(), null);
		if (bodegaRepository.crear(datos) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "La sede no existe");
		}
		return bodegaRepository.listar();
	}

	@Override
	@Transactional
	public List<Bodega> actualizar(String codigo, DatosBodega datos) {
		validarNombre(datos.nombre(), codigo);
		if (datos.activa() == null) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Indique si la bodega está activa");
		}
		if (bodegaRepository.actualizar(codigo, datos) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "Bodega no encontrada");
		}
		return bodegaRepository.listar();
	}

	private void validarNombre(String nombre, String codigo) {
		if (nombre == null || nombre.isBlank()) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Escriba el nombre de la bodega");
		}
		if (bodegaRepository.nombreUsado(nombre.trim(), codigo)) {
			throw new NegocioException(HttpStatus.CONFLICT, "Ya existe una bodega con ese nombre");
		}
	}

}
