package co.brangcol.inventario.service.impl;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.NuevaProduccion;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Produccion;
import co.brangcol.inventario.repository.ProduccionRepository;
import co.brangcol.inventario.service.ProduccionService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.shared.texto.Textos;

@Service
public class ProduccionServiceImpl implements ProduccionService {

	private final ProduccionRepository produccionRepository;

	private final VerificadorPermisos permisos;

	public ProduccionServiceImpl(ProduccionRepository produccionRepository, VerificadorPermisos permisos) {
		this.produccionRepository = produccionRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public Produccion registrar(NuevaProduccion produccion) {
		if (produccion.item() == null || produccion.bodega() == null) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Elija el producto y la bodega");
		}
		if (produccion.cantidad() == null || produccion.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Escriba cuántas unidades produjo");
		}
		String codigo = produccionRepository.registrar(produccion.item(), produccion.bodega(), produccion.cantidad(),
				Textos.vacioANulo(produccion.observacion()));
		return produccionRepository.obtener(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Produccion> listar(int pagina, int tamano) {
		permisos.exigir("inventario:existencia:consultar");
		return produccionRepository.listar(pagina, tamano);
	}

	@Override
	@Transactional(readOnly = true)
	public Produccion obtener(String codigo) {
		permisos.exigir("inventario:existencia:consultar");
		Produccion produccion = produccionRepository.obtener(codigo);
		if (produccion == null) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "Producción no encontrada");
		}
		return produccion;
	}

	@Override
	@Transactional
	public Produccion anular(String codigo, String motivo) {
		produccionRepository.anular(codigo, Motivo.validar(motivo));
		return produccionRepository.obtener(codigo);
	}

}
