package co.brangcol.producto.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.brangcol.impuesto.service.ImpuestoService;
import co.brangcol.parametro.model.Unidad;
import co.brangcol.parametro.service.UnidadService;
import co.brangcol.producto.model.DatosItem;
import co.brangcol.producto.model.UnidadAlterna;
import co.brangcol.producto.service.CategoriaService;
import co.brangcol.producto.service.MarcaService;
import co.brangcol.shared.error.NegocioException;

// Mismas reglas que la base de datos, con mensajes claros antes de intentar guardar
@Component
public class ValidadorItem {

	private static final Pattern SKU = Pattern.compile("^[A-Za-z0-9._-]{1,40}$");

	private static final Pattern CODIGO_BARRAS = Pattern.compile("^[A-Za-z0-9-]{4,48}$");

	private static final Set<String> TIPOS = Set.of("SIMPLE", "COMPUESTO", "FABRICADO", "INSUMO", "SERVICIO");

	private final UnidadService unidadService;

	private final ImpuestoService impuestoService;

	private final CategoriaService categoriaService;

	private final MarcaService marcaService;

	public ValidadorItem(UnidadService unidadService, ImpuestoService impuestoService,
			CategoriaService categoriaService, MarcaService marcaService) {
		this.unidadService = unidadService;
		this.impuestoService = impuestoService;
		this.categoriaService = categoriaService;
		this.marcaService = marcaService;
	}

	public void validar(DatosItem d) {
		if (!TIPOS.contains(d.tipo())) {
			fallar("El tipo de producto no es válido");
		}
		if (d.nombre() == null || d.nombre().length() > 200) {
			fallar("Escriba el nombre del producto (máximo 200 caracteres)");
		}
		if (d.sku() != null && !SKU.matcher(d.sku()).matches()) {
			fallar("El código interno solo admite letras, números, punto, guion y guion bajo");
		}
		if (d.descripcion() != null && d.descripcion().length() > 2000) {
			fallar("La descripción es demasiado larga");
		}
		Unidad unidad = unidadService.buscar(d.unidad());
		if (unidad == null) {
			fallar("La unidad de medida no es válida");
		}
		if (d.permiteFraccion() && !unidad.permiteFraccion()) {
			fallar("La unidad " + unidad.nombre() + " no admite cantidades con decimales");
		}
		if (d.impuesto() != null && impuestoService.buscar(d.impuesto()) == null) {
			fallar("El impuesto no es válido");
		}
		if (d.categoria() != null) {
			categoriaService.validar(d.categoria());
		}
		if (d.marca() != null) {
			marcaService.validar(d.marca());
		}
		if (d.precioVenta() == null || d.precioVenta().compareTo(BigDecimal.ZERO) < 0) {
			fallar("El precio no puede ser negativo");
		}
		validarCodigosBarras(d);
		validarUnidades(d);
	}

	private void validarCodigosBarras(DatosItem d) {
		Set<String> vistos = new HashSet<>();
		for (String codigo : d.codigosBarras()) {
			if (!CODIGO_BARRAS.matcher(codigo).matches()) {
				fallar("El código de barras " + codigo + " no es válido");
			}
			if (!vistos.add(codigo)) {
				fallar("El código de barras " + codigo + " está repetido");
			}
		}
	}

	private void validarUnidades(DatosItem d) {
		Set<String> vistas = new HashSet<>();
		for (UnidadAlterna u : d.unidades()) {
			if (unidadService.buscar(u.unidad()) == null) {
				fallar("Una de las unidades alternas no es válida");
			}
			if (u.unidad().equals(d.unidad())) {
				fallar("La unidad alterna no puede ser la misma unidad base");
			}
			if (!vistas.add(u.unidad())) {
				fallar("Hay una unidad alterna repetida");
			}
			if (u.factor() == null || u.factor().compareTo(BigDecimal.ZERO) <= 0) {
				fallar("La equivalencia de cada unidad alterna debe ser mayor que cero");
			}
			if (u.precioVenta() != null && u.precioVenta().compareTo(BigDecimal.ZERO) < 0) {
				fallar("El precio de una unidad alterna no puede ser negativo");
			}
			if (!u.paraCompra() && !u.paraVenta()) {
				fallar("Cada unidad alterna debe servir para comprar, para vender o para ambas");
			}
		}
	}

	private void fallar(String mensaje) {
		throw new NegocioException(HttpStatus.BAD_REQUEST, mensaje);
	}

}
