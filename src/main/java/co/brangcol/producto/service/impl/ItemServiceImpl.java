package co.brangcol.producto.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.producto.model.DatosItem;
import co.brangcol.producto.model.Item;
import co.brangcol.producto.repository.CodigoBarrasRepository;
import co.brangcol.producto.repository.ItemRepository;
import co.brangcol.producto.repository.ItemUnidadRepository;
import co.brangcol.producto.service.ItemService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;

@Service
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;

	private final CodigoBarrasRepository codigoBarrasRepository;

	private final ItemUnidadRepository itemUnidadRepository;

	private final ValidadorItem validador;

	private final VerificadorPermisos permisos;

	public ItemServiceImpl(ItemRepository itemRepository, CodigoBarrasRepository codigoBarrasRepository,
			ItemUnidadRepository itemUnidadRepository, ValidadorItem validador, VerificadorPermisos permisos) {
		this.itemRepository = itemRepository;
		this.codigoBarrasRepository = codigoBarrasRepository;
		this.itemUnidadRepository = itemUnidadRepository;
		this.validador = validador;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public Item crear(DatosItem datos) {
		DatosItem conSku = datos.sku() == null ? conSku(datos, itemRepository.siguienteSku()) : datos;
		validador.validar(conSku);
		validarUnicos(conSku, null);
		String codigo = itemRepository.crear(conSku);
		guardarDetalle(codigo, conSku);
		return detalle(codigo);
	}

	@Override
	@Transactional
	public Item actualizar(String codigo, DatosItem datos) {
		Item actual = detalle(codigo);
		DatosItem conSku = datos.sku() == null ? conSku(datos, actual.sku()) : datos;
		validador.validar(conSku);
		validarUnicos(conSku, codigo);
		itemRepository.actualizar(codigo, conSku);
		guardarDetalle(codigo, conSku);
		return detalle(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Item obtener(String codigo) {
		permisos.exigir("productos:item:consultar");
		return detalle(codigo);
	}

	@Override
	@Transactional
	public Item cambiarEstado(String codigo, boolean activo) {
		if (itemRepository.cambiarEstado(codigo, activo) == 0) {
			throw noEncontrado();
		}
		return detalle(codigo);
	}

	private Item detalle(String codigo) {
		Item item = itemRepository.obtener(codigo);
		if (item == null) {
			throw noEncontrado();
		}
		item.codigosBarras().addAll(codigoBarrasRepository.listar(codigo));
		item.unidades().addAll(itemUnidadRepository.listar(codigo));
		return item;
	}

	private void validarUnicos(DatosItem datos, String codigo) {
		String conSku = itemRepository.nombrePorSku(datos.sku());
		if (conSku != null && !conSku.startsWith(codigo + "|")) {
			throw new NegocioException(HttpStatus.CONFLICT,
					"El código interno " + datos.sku() + " ya lo usa " + conSku.substring(conSku.indexOf('|') + 1));
		}
		for (String barras : datos.codigosBarras()) {
			String otro = codigoBarrasRepository.usadoPorOtro(barras, codigo);
			if (otro != null) {
				throw new NegocioException(HttpStatus.CONFLICT, "El código de barras " + barras + " ya lo usa " + otro);
			}
		}
	}

	private void guardarDetalle(String codigo, DatosItem datos) {
		codigoBarrasRepository.reemplazar(codigo, datos.codigosBarras());
		itemUnidadRepository.reemplazar(codigo, datos.unidades());
	}

	private DatosItem conSku(DatosItem d, String sku) {
		return new DatosItem(d.tipo(), sku, d.nombre(), d.descripcion(), d.categoria(), d.marca(), d.unidad(),
				d.permiteFraccion(), d.impuesto(), d.precioVenta(), d.seVende(), d.seCompra(), d.codigosBarras(),
				d.unidades());
	}

	private NegocioException noEncontrado() {
		return new NegocioException(HttpStatus.NOT_FOUND, "El producto no existe");
	}

}
