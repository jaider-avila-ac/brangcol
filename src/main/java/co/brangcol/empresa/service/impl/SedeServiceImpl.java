package co.brangcol.empresa.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.empresa.model.DatosSede;
import co.brangcol.empresa.model.Sede;
import co.brangcol.empresa.repository.SedeRepository;
import co.brangcol.empresa.service.SedeService;
import co.brangcol.parametro.service.MunicipioService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class SedeServiceImpl implements SedeService {

	private final SedeRepository sedeRepository;

	private final MunicipioService municipioService;

	private final VerificadorPermisos permisos;

	public SedeServiceImpl(SedeRepository sedeRepository, MunicipioService municipioService,
			VerificadorPermisos permisos) {
		this.sedeRepository = sedeRepository;
		this.municipioService = municipioService;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sede> listar() {
		permisos.exigir("plataforma:empresa:consultar");
		return sedeRepository.listar();
	}

	@Override
	@Transactional
	public Sede crear(DatosSede datos) {
		municipioService.validarMunicipio(datos.municipioCodigo());
		String codigo = sedeRepository.crear(datos);
		return sedeRepository.obtener(codigo);
	}

	@Override
	@Transactional
	public Sede actualizar(String codigo, DatosSede datos) {
		municipioService.validarMunicipio(datos.municipioCodigo());
		if (sedeRepository.actualizar(codigo, datos) == 0) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "La sede no existe");
		}
		return sedeRepository.obtener(codigo);
	}

}
