package co.brangcol.impuesto.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.impuesto.model.Impuesto;
import co.brangcol.impuesto.repository.ImpuestoRepository;
import co.brangcol.impuesto.service.ImpuestoService;

@Service
public class ImpuestoServiceImpl implements ImpuestoService {

	private final ImpuestoRepository impuestoRepository;

	public ImpuestoServiceImpl(ImpuestoRepository impuestoRepository) {
		this.impuestoRepository = impuestoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Impuesto> activos() {
		return impuestoRepository.activos();
	}

	@Override
	@Transactional(readOnly = true)
	public Impuesto buscar(String codigo) {
		for (Impuesto impuesto : impuestoRepository.activos()) {
			if (impuesto.codigo().equals(codigo)) {
				return impuesto;
			}
		}
		return null;
	}

}
