package co.brangcol.impuesto.service;

import java.util.List;

import co.brangcol.impuesto.model.Impuesto;

public interface ImpuestoService {

	List<Impuesto> activos();

	Impuesto buscar(String codigo);

}
