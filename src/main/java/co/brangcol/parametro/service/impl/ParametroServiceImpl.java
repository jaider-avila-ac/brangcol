package co.brangcol.parametro.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.parametro.model.Municipio;
import co.brangcol.parametro.repository.ParametroRepository;
import co.brangcol.parametro.service.ParametroService;
import co.brangcol.shared.error.NegocioException;

@Service
public class ParametroServiceImpl implements ParametroService {

	private final ParametroRepository parametroRepository;

	public ParametroServiceImpl(ParametroRepository parametroRepository) {
		this.parametroRepository = parametroRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Municipio> buscarMunicipios(String texto) {
		String limpio = texto == null ? "" : texto.trim();
		return parametroRepository.buscarMunicipios(limpio);
	}

	@Override
	@Transactional(readOnly = true)
	public void validarMunicipio(String codigo) {
		if (!parametroRepository.existeMunicipio(codigo)) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "El municipio no es válido");
		}
	}

}
