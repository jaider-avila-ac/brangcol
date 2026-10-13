package co.brangcol.inventario.service.impl;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.Existencia;
import co.brangcol.inventario.model.FiltroExistencias;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.ResumenInventario;
import co.brangcol.inventario.repository.ExistenciaRepository;
import co.brangcol.inventario.service.ExistenciaService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class ExistenciaServiceImpl implements ExistenciaService {

	private final ExistenciaRepository existenciaRepository;

	private final VerificadorPermisos permisos;

	public ExistenciaServiceImpl(ExistenciaRepository existenciaRepository, VerificadorPermisos permisos) {
		this.existenciaRepository = existenciaRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Existencia> buscar(FiltroExistencias filtro) {
		permisos.exigir("inventario:existencia:consultar");
		return existenciaRepository.buscar(filtro);
	}

	@Override
	@Transactional(readOnly = true)
	public ResumenInventario resumen() {
		permisos.exigir("inventario:existencia:consultar");
		return existenciaRepository.resumen();
	}

	@Override
	@Transactional
	public void definirMinimo(String bodega, String item, BigDecimal minimo) {
		if (minimo != null && minimo.compareTo(BigDecimal.ZERO) < 0) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "El mínimo no puede ser negativo");
		}
		if (existenciaRepository.definirMinimo(bodega, item, minimo) == 0) {
			throw new NegocioException(HttpStatus.CONFLICT,
					"Registre primero el inventario inicial de este producto en la bodega");
		}
	}

}
