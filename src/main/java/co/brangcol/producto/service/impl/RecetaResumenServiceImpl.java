package co.brangcol.producto.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.producto.model.RecetaResumen;
import co.brangcol.producto.repository.RecetaResumenRepository;
import co.brangcol.producto.service.RecetaResumenService;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class RecetaResumenServiceImpl implements RecetaResumenService {

	private final RecetaResumenRepository recetaResumenRepository;

	private final VerificadorPermisos permisos;

	public RecetaResumenServiceImpl(RecetaResumenRepository recetaResumenRepository, VerificadorPermisos permisos) {
		this.recetaResumenRepository = recetaResumenRepository;
		this.permisos = permisos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RecetaResumen> listar(String texto) {
		permisos.exigir("productos:item:consultar");
		return recetaResumenRepository.listar(texto);
	}

}
