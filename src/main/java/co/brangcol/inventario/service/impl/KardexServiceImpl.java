package co.brangcol.inventario.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.FiltroKardex;
import co.brangcol.inventario.model.Movimiento;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.repository.KardexRepository;
import co.brangcol.inventario.service.KardexService;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class KardexServiceImpl implements KardexService {

	private final KardexRepository kardexRepository;

	private final VerificadorPermisos permisos;

	public KardexServiceImpl(KardexRepository kardexRepository, VerificadorPermisos permisos) {
		this.kardexRepository = kardexRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Movimiento> buscar(FiltroKardex filtro) {
		permisos.exigir("inventario:existencia:consultar");
		return kardexRepository.buscar(filtro);
	}

}
