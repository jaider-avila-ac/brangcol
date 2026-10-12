package co.brangcol.parametro.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.parametro.model.Unidad;
import co.brangcol.parametro.repository.UnidadRepository;
import co.brangcol.parametro.service.UnidadService;

@Service
public class UnidadServiceImpl implements UnidadService {

	private final UnidadRepository unidadRepository;

	public UnidadServiceImpl(UnidadRepository unidadRepository) {
		this.unidadRepository = unidadRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Unidad> todas() {
		return unidadRepository.todas();
	}

	@Override
	@Transactional(readOnly = true)
	public Unidad buscar(String codigo) {
		for (Unidad unidad : unidadRepository.todas()) {
			if (unidad.codigo().equals(codigo)) {
				return unidad;
			}
		}
		return null;
	}

}
