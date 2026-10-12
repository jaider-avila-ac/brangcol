package co.brangcol.tercero.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.tercero.model.Coincidencia;
import co.brangcol.tercero.model.DatosTercero;
import co.brangcol.tercero.model.Tercero;
import co.brangcol.tercero.repository.ClienteRepository;
import co.brangcol.tercero.repository.ProveedorRepository;
import co.brangcol.tercero.repository.TerceroRepository;
import co.brangcol.tercero.service.TerceroService;

@Service
public class TerceroServiceImpl implements TerceroService {

	private final TerceroRepository terceroRepository;

	private final ClienteRepository clienteRepository;

	private final ProveedorRepository proveedorRepository;

	private final ValidadorTercero validador;

	private final VerificadorPermisos permisos;

	public TerceroServiceImpl(TerceroRepository terceroRepository, ClienteRepository clienteRepository,
			ProveedorRepository proveedorRepository, ValidadorTercero validador, VerificadorPermisos permisos) {
		this.terceroRepository = terceroRepository;
		this.clienteRepository = clienteRepository;
		this.proveedorRepository = proveedorRepository;
		this.validador = validador;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public Tercero crear(DatosTercero datos) {
		validador.validar(datos);
		Coincidencia existente = buscarPorDocumento(datos.tipoDocumento(), datos.numeroDocumento());
		if (existente != null) {
			throw new NegocioException(HttpStatus.CONFLICT,
					"Ya está registrado " + existente.nombre() + " con ese documento");
		}
		String codigo = terceroRepository.crear(datos);
		guardarRoles(codigo, datos);
		return terceroRepository.obtener(codigo);
	}

	@Override
	@Transactional
	public Tercero actualizar(String codigo, DatosTercero datos) {
		validador.validar(datos);
		Coincidencia existente = buscarPorDocumento(datos.tipoDocumento(), datos.numeroDocumento());
		if (existente != null && !existente.codigo().equals(codigo)) {
			throw new NegocioException(HttpStatus.CONFLICT,
					"Ese documento ya pertenece a " + existente.nombre());
		}
		if (terceroRepository.actualizar(codigo, datos) == 0) {
			throw noEncontrado();
		}
		guardarRoles(codigo, datos);
		return terceroRepository.obtener(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Tercero obtener(String codigo) {
		permisos.exigir("terceros:tercero:consultar");
		Tercero tercero = terceroRepository.obtener(codigo);
		if (tercero == null) {
			throw noEncontrado();
		}
		return tercero;
	}

	@Override
	@Transactional
	public Tercero cambiarEstado(String codigo, boolean activo) {
		if (terceroRepository.cambiarEstado(codigo, activo) == 0) {
			throw noEncontrado();
		}
		return terceroRepository.obtener(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Coincidencia buscarPorDocumento(String tipoDocumento, String numero) {
		if (tipoDocumento == null || numero == null) {
			return null;
		}
		return terceroRepository.buscarPorDocumento(tipoDocumento, numero);
	}

	@Override
	@Transactional
	public boolean agregarRoles(String codigo, DatosTercero datos) {
		int agregados = 0;
		if (datos.cliente() != null) {
			agregados += clienteRepository.agregarSiNoExiste(codigo, datos.cliente());
		}
		if (datos.proveedor() != null) {
			agregados += proveedorRepository.agregarSiNoExiste(codigo, datos.proveedor());
		}
		return agregados > 0;
	}

	private void guardarRoles(String codigo, DatosTercero datos) {
		if (datos.cliente() != null) {
			clienteRepository.guardar(codigo, datos.cliente());
		}
		if (datos.proveedor() != null) {
			proveedorRepository.guardar(codigo, datos.proveedor());
		}
	}

	private NegocioException noEncontrado() {
		return new NegocioException(HttpStatus.NOT_FOUND, "El cliente o proveedor no existe");
	}

}
