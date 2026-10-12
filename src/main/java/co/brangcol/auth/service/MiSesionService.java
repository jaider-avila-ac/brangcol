package co.brangcol.auth.service;

import java.util.List;

import co.brangcol.auth.model.ImagenesSesion;
import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;

public interface MiSesionService {

	SesionUsuario miSesion();

	ImagenesSesion imagenes();

	List<SesionAbierta> misSesiones();

	void cerrarSesionAbierta(String codigo);

	void cerrarOtrasSesiones();

}
