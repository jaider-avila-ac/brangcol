package co.brangcol.tercero.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.shared.web.PageResponse;
import co.brangcol.tercero.dto.CoincidenciaResponse;
import co.brangcol.tercero.dto.TerceroResumenResponse;
import co.brangcol.tercero.model.Coincidencia;
import co.brangcol.tercero.model.FiltroTerceros;
import co.brangcol.tercero.model.PaginaTerceros;
import co.brangcol.tercero.model.TerceroResumen;

@Component
public class TerceroResumenMapper {

	public PageResponse<TerceroResumenResponse> aPagina(PaginaTerceros pagina, FiltroTerceros filtro) {
		List<TerceroResumenResponse> contenido = new ArrayList<>();
		for (TerceroResumen t : pagina.terceros()) {
			contenido.add(new TerceroResumenResponse(t.codigo(), t.nombre(), documento(t), t.telefono(), t.correo(),
					t.municipio(), t.esCliente(), t.esProveedor(), t.debe(), t.vencido(), t.saldoFavor(),
					t.leDebemos(), t.activo(), "COMPLETO".equals(t.estadoDatos())));
		}
		int paginas = (int) ((pagina.total() + filtro.tamano() - 1) / filtro.tamano());
		return new PageResponse<>(contenido, filtro.pagina(), filtro.tamano(), pagina.total(), paginas);
	}

	public CoincidenciaResponse aCoincidencia(Coincidencia c) {
		return new CoincidenciaResponse(c.codigo(), c.nombre(), c.esCliente(), c.esProveedor());
	}

	private String documento(TerceroResumen t) {
		if (t.numeroDocumento() == null) {
			return null;
		}
		String numero = t.numeroDocumento() + (t.dv() == null ? "" : "-" + t.dv());
		return t.tipoDocumento() + " " + numero;
	}

}
