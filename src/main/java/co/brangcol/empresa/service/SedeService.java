package co.brangcol.empresa.service;

import java.util.List;

import co.brangcol.empresa.model.DatosSede;
import co.brangcol.empresa.model.Sede;

public interface SedeService {

	List<Sede> listar();

	Sede crear(DatosSede datos);

	Sede actualizar(String codigo, DatosSede datos);

}
