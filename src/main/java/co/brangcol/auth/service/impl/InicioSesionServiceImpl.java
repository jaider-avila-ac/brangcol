package co.brangcol.auth.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.ResultadoInicio;
import co.brangcol.auth.repository.InicioSesionRepository;
import co.brangcol.auth.service.InicioSesionService;
import co.brangcol.shared.web.ClienteWeb;

@Service
public class InicioSesionServiceImpl implements InicioSesionService {

	private final InicioSesionRepository inicioSesionRepository;

	public InicioSesionServiceImpl(InicioSesionRepository inicioSesionRepository) {
		this.inicioSesionRepository = inicioSesionRepository;
	}

	// Los intentos fallidos se registran en la base: no se lanza excepcion aqui
	// para que la transaccion se confirme y el intento quede contado
	@Override
	@Transactional
	public ResultadoInicio iniciarSesion(String correo, String contrasena, String empresa, ClienteWeb cliente) {
		return inicioSesionRepository.iniciarSesion(correo, contrasena, empresa, cliente.ip(), cliente.agente());
	}

	@Override
	@Transactional
	public List<EmpresaDisponible> listarEmpresas(String correo, String contrasena, ClienteWeb cliente) {
		return inicioSesionRepository.listarEmpresas(correo, contrasena, cliente.ip(), cliente.agente());
	}

	@Override
	@Transactional
	public void cerrarSesion(String token) {
		inicioSesionRepository.cerrarSesion(token);
	}

}
