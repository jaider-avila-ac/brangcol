package co.brangcol.inventario.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.NuevoConteo;
import co.brangcol.inventario.repository.ConteoRepository;
import co.brangcol.inventario.service.ConteoService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.texto.Textos;

@Service
public class ConteoServiceImpl implements ConteoService {

	private final ConteoRepository conteoRepository;

	private final LineasJson lineasJson;

	public ConteoServiceImpl(ConteoRepository conteoRepository, LineasJson lineasJson) {
		this.conteoRepository = conteoRepository;
		this.lineasJson = lineasJson;
	}

	@Override
	@Transactional
	public String registrar(NuevoConteo conteo) {
		if (conteo.bodega() == null) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Elija la bodega que contó");
		}
		return conteoRepository.registrar(conteo.bodega(), Textos.vacioANulo(conteo.observacion()),
				lineasJson.cantidades(conteo.lineas(), true));
	}

}
