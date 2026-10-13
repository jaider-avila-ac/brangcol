package co.brangcol.inventario.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.inventario.model.LineaInventario;
import co.brangcol.inventario.model.NuevoTraslado;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.inventario.model.Traslado;
import co.brangcol.inventario.model.TrasladoResumen;
import co.brangcol.inventario.repository.TrasladoRepository;
import co.brangcol.inventario.service.TrasladoService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.shared.texto.Textos;

@Service
public class TrasladoServiceImpl implements TrasladoService {

	private final TrasladoRepository trasladoRepository;

	private final LineasJson lineasJson;

	private final VerificadorPermisos permisos;

	public TrasladoServiceImpl(TrasladoRepository trasladoRepository, LineasJson lineasJson,
			VerificadorPermisos permisos) {
		this.trasladoRepository = trasladoRepository;
		this.lineasJson = lineasJson;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public Traslado registrar(NuevoTraslado traslado) {
		if (traslado.origen() == null || traslado.destino() == null) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "Elija la bodega de origen y la de destino");
		}
		String codigo = trasladoRepository.registrar(traslado.origen(), traslado.destino(),
				Textos.vacioANulo(traslado.observacion()), lineasJson.cantidades(traslado.lineas(), false));
		return trasladoRepository.obtener(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<TrasladoResumen> listar(int pagina, int tamano) {
		permisos.exigir("inventario:existencia:consultar");
		return trasladoRepository.listar(pagina, tamano);
	}

	@Override
	@Transactional(readOnly = true)
	public Traslado obtener(String codigo) {
		permisos.exigir("inventario:existencia:consultar");
		Traslado traslado = trasladoRepository.obtener(codigo);
		if (traslado == null) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "Traslado no encontrado");
		}
		return traslado;
	}

	// Sin lineas se recibe todo lo enviado
	@Override
	@Transactional
	public Traslado recibir(String codigo, List<LineaInventario> recibidos) {
		String json = null;
		if (recibidos != null && !recibidos.isEmpty()) {
			json = lineasJson.cantidades(recibidos, true);
		}
		trasladoRepository.recibir(codigo, json);
		return trasladoRepository.obtener(codigo);
	}

	@Override
	@Transactional
	public Traslado anular(String codigo, String motivo) {
		trasladoRepository.anular(codigo, Motivo.validar(motivo));
		return trasladoRepository.obtener(codigo);
	}

}
