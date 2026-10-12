package co.brangcol.empresa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.empresa.model.DatosEmpresa;
import co.brangcol.empresa.model.Empresa;
import co.brangcol.empresa.repository.EmpresaRepository;
import co.brangcol.empresa.service.EmpresaService;
import co.brangcol.parametro.service.ParametroService;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class EmpresaServiceImpl implements EmpresaService {

	private final EmpresaRepository empresaRepository;

	private final ParametroService parametroService;

	private final VerificadorPermisos permisos;

	public EmpresaServiceImpl(EmpresaRepository empresaRepository, ParametroService parametroService,
			VerificadorPermisos permisos) {
		this.empresaRepository = empresaRepository;
		this.parametroService = parametroService;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public Empresa obtener() {
		permisos.exigir("plataforma:empresa:consultar");
		return empresaRepository.obtener();
	}

	@Override
	@Transactional
	public Empresa actualizar(DatosEmpresa datos) {
		parametroService.validarMunicipio(datos.municipioCodigo());
		empresaRepository.actualizar(datos);
		return empresaRepository.obtener();
	}

}
