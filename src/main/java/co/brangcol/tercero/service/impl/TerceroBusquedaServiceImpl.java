package co.brangcol.tercero.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.tercero.model.FiltroTerceros;
import co.brangcol.tercero.model.PaginaTerceros;
import co.brangcol.tercero.repository.TerceroBusquedaRepository;
import co.brangcol.tercero.service.TerceroBusquedaService;

@Service
public class TerceroBusquedaServiceImpl implements TerceroBusquedaService {

	private final TerceroBusquedaRepository terceroBusquedaRepository;

	private final VerificadorPermisos permisos;

	public TerceroBusquedaServiceImpl(TerceroBusquedaRepository terceroBusquedaRepository,
			VerificadorPermisos permisos) {
		this.terceroBusquedaRepository = terceroBusquedaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaTerceros buscar(FiltroTerceros filtro) {
		permisos.exigir("terceros:tercero:consultar");
		return terceroBusquedaRepository.buscar(filtro);
	}

}
