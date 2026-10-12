package co.brangcol.empresa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.empresa.model.ConfiguracionEmpresa;
import co.brangcol.empresa.repository.ConfiguracionRepository;
import co.brangcol.empresa.service.ConfiguracionService;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class ConfiguracionServiceImpl implements ConfiguracionService {

	private final ConfiguracionRepository configuracionRepository;

	private final VerificadorPermisos permisos;

	public ConfiguracionServiceImpl(ConfiguracionRepository configuracionRepository, VerificadorPermisos permisos) {
		this.configuracionRepository = configuracionRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public ConfiguracionEmpresa obtener() {
		permisos.exigir("plataforma:empresa:consultar");
		return configuracionRepository.obtener();
	}

	@Override
	@Transactional
	public ConfiguracionEmpresa actualizar(ConfiguracionEmpresa configuracion) {
		configuracionRepository.actualizar(configuracion);
		return configuracionRepository.obtener();
	}

}
