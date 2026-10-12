package co.brangcol.parametro.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.parametro.model.Municipio;
import co.brangcol.parametro.repository.MunicipioRepository;
import co.brangcol.parametro.service.MunicipioService;
import co.brangcol.shared.error.NegocioException;

@Service
public class MunicipioServiceImpl implements MunicipioService {

	private final MunicipioRepository municipioRepository;

	public MunicipioServiceImpl(MunicipioRepository municipioRepository) {
		this.municipioRepository = municipioRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Municipio> buscarMunicipios(String texto) {
		String limpio = texto == null ? "" : texto.trim();
		return municipioRepository.buscarMunicipios(limpio);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Municipio> todos() {
		return municipioRepository.todos();
	}

	@Override
	@Transactional(readOnly = true)
	public void validarMunicipio(String codigo) {
		if (!municipioRepository.existeMunicipio(codigo)) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "El municipio no es válido");
		}
	}

}
