package co.brangcol.auth.service;

import java.util.List;

import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;

public interface MiSesionService {

	SesionUsuario miSesion();

	List<SesionAbierta> misSesiones();

	void cerrarSesionAbierta(String codigo);

	void cerrarOtrasSesiones();

}
