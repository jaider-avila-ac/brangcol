package co.brangcol.inventario.service.impl;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.Ajuste;
import co.brangcol.inventario.model.AjusteResumen;
import co.brangcol.inventario.model.NuevoAjuste;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.repository.AjusteRepository;
import co.brangcol.inventario.service.AjusteService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class AjusteServiceImpl implements AjusteService {

	private static final Set<String> CLASES = Set.of("AJUSTE", "INICIAL", "CONSUMO_INTERNO");

	private final AjusteRepository ajusteRepository;

	private final LineasJson lineasJson;

	private final VerificadorPermisos permisos;

	public AjusteServiceImpl(AjusteRepository ajusteRepository, LineasJson lineasJson, VerificadorPermisos permisos) {
		this.ajusteRepository = ajusteRepository;
		this.lineasJson = lineasJson;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public Ajuste registrar(NuevoAjuste ajuste) {
		if (!CLASES.contains(ajuste.clase())) {
			fallar("El tipo de ajuste no es válido");
		}
		if (ajuste.bodega() == null) {
			fallar("Elija la bodega");
		}
		String motivo = Motivo.validar(ajuste.motivo());
		String codigo = ajusteRepository.registrar(ajuste.clase(), ajuste.bodega(), motivo,
				lineasJson.conSentido(ajuste.lineas()));
		return ajusteRepository.obtener(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<AjusteResumen> listar(int pagina, int tamano) {
		permisos.exigir("inventario:existencia:consultar");
		return ajusteRepository.listar(pagina, tamano);
	}

	@Override
	@Transactional(readOnly = true)
	public Ajuste obtener(String codigo) {
		permisos.exigir("inventario:existencia:consultar");
		Ajuste ajuste = ajusteRepository.obtener(codigo);
		if (ajuste == null) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "Ajuste no encontrado");
		}
		return ajuste;
	}

	@Override
	@Transactional
	public Ajuste anular(String codigo, String motivo) {
		ajusteRepository.anular(codigo, Motivo.validar(motivo));
		return ajusteRepository.obtener(codigo);
	}

	private void fallar(String mensaje) {
		throw new NegocioException(HttpStatus.BAD_REQUEST, mensaje);
	}

}
