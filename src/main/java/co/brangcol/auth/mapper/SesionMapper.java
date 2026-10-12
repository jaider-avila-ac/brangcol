package co.brangcol.auth.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.auth.dto.SesionResponse;
import co.brangcol.auth.dto.YoResponse;
import co.brangcol.auth.model.ImagenesSesion;
import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;

@Component
public class SesionMapper {

	public YoResponse aYo(SesionUsuario sesion, ImagenesSesion imagenes) {
		YoResponse.Persona persona = new YoResponse.Persona(sesion.personaCodigo(), sesion.correo(), sesion.nombres(),
				sesion.apellidos());
		YoResponse.Empresa empresa = new YoResponse.Empresa(sesion.empresaCodigo(), sesion.razonSocial(),
				sesion.nombreComercial(), sesion.empresaEstado());
		YoResponse.Rol rol = new YoResponse.Rol(sesion.rolNombre(), sesion.rolClave(), sesion.esPropietario());
		YoResponse.Plan plan = new YoResponse.Plan(sesion.planCodigo(), sesion.planNombre(),
				sesion.suscripcionEstado());
		return new YoResponse(persona, empresa, rol, plan, sesion.minutosInactividad(), imagenes.foto(), imagenes.logo(),
				sesion.funcionalidades(),
				sesion.permisos());
	}

	public List<SesionResponse> aSesiones(List<SesionAbierta> sesiones) {
		List<SesionResponse> respuesta = new ArrayList<>();
		for (SesionAbierta sesion : sesiones) {
			respuesta.add(new SesionResponse(sesion.codigo(), sesion.empresa(), sesion.ip(), sesion.agente(),
					sesion.iniciadaEn(), sesion.ultimaActividad(), sesion.esActual()));
		}
		return respuesta;
	}

}
