package co.brangcol.parametro.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.parametro.model.TipoDocumento;
import co.brangcol.parametro.repository.TipoDocumentoRepository;
import co.brangcol.parametro.service.TipoDocumentoService;
import co.brangcol.shared.error.NegocioException;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

	private final TipoDocumentoRepository tipoDocumentoRepository;

	public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository) {
		this.tipoDocumentoRepository = tipoDocumentoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipoDocumento> todos() {
		return tipoDocumentoRepository.todos();
	}

	@Override
	@Transactional(readOnly = true)
	public TipoDocumento buscar(String codigoOAbreviatura) {
		if (codigoOAbreviatura == null) {
			return null;
		}
		String texto = codigoOAbreviatura.trim();
		for (TipoDocumento tipo : tipoDocumentoRepository.todos()) {
			if (tipo.codigo().equals(texto) || tipo.abreviatura().equalsIgnoreCase(texto)) {
				return tipo;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public void validar(String codigo) {
		if (buscar(codigo) == null) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "El tipo de documento no es válido");
		}
	}

}
