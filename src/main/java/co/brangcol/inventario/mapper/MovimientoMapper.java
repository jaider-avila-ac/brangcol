package co.brangcol.inventario.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import co.brangcol.inventario.dto.MovimientoResponse;
import co.brangcol.inventario.model.FiltroKardex;
import co.brangcol.inventario.model.Movimiento;
import co.brangcol.inventario.model.Pagina;
import co.brangcol.shared.web.PageResponse;

@Component
public class MovimientoMapper {

	private static final Map<String, String> DOCUMENTOS = Map.of("AJUSTE", "Ajuste", "TRASLADO", "Traslado",
			"FACTURA", "Factura", "COMPRA", "Compra", "ENSAMBLE", "Producción", "NOTA", "Nota");

	// La cantidad sale en la columna de entrada o de salida segun el signo del movimiento
	public PageResponse<MovimientoResponse> aPagina(Pagina<Movimiento> pagina, FiltroKardex filtro) {
		List<MovimientoResponse> contenido = new ArrayList<>();
		for (Movimiento m : pagina.items()) {
			boolean entra = m.signo() > 0;
			contenido.add(new MovimientoResponse(m.fecha(), m.tipo(), m.tipoNombre(), entra ? m.cantidad() : null,
					entra ? null : m.cantidad(), m.saldo(), m.costoUnitario(), m.costoPromedio(), m.bodega(),
					documento(m), m.observacion()));
		}
		return Paginas.de(contenido, pagina.total(), filtro.pagina(), filtro.tamano());
	}

	private String documento(Movimiento m) {
		String nombre = DOCUMENTOS.getOrDefault(m.documentoTipo(), m.documentoTipo());
		if (m.documentoNumero() == null) {
			return nombre;
		}
		return nombre + " " + m.documentoNumero();
	}

}
