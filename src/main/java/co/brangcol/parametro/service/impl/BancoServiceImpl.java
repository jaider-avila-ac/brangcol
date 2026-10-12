package co.brangcol.parametro.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.parametro.repository.BancoRepository;
import co.brangcol.parametro.service.BancoService;
import co.brangcol.shared.error.NegocioException;

@Service
public class BancoServiceImpl implements BancoService {

	private final BancoRepository bancoRepository;

	public BancoServiceImpl(BancoRepository bancoRepository) {
		this.bancoRepository = bancoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> nombres() {
		return bancoRepository.nombres();
	}

	@Override
	@Transactional(readOnly = true)
	public void validar(String nombre) {
		if (!bancoRepository.nombres().contains(nombre)) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "El banco no es válido");
		}
	}

}
