package co.brangcol.auth.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.auth.dto.EmpresaOpcionResponse;
import co.brangcol.auth.dto.SesionResponse;
import co.brangcol.auth.dto.YoResponse;
import co.brangcol.auth.model.EmpresaDisponible;
import co.brangcol.auth.model.SesionAbierta;
import co.brangcol.auth.model.SesionUsuario;

@Component
public class AuthMapper {

	public YoResponse aYo(SesionUsuario sesion) {
		YoResponse.Persona persona = new YoResponse.Persona(sesion.personaCodigo(), sesion.correo(), sesion.nombres(),
				sesion.apellidos());
		YoResponse.Empresa empresa = new YoResponse.Empresa(sesion.empresaCodigo(), sesion.razonSocial(),
				sesion.nombreComercial(), sesion.empresaEstado());
		YoResponse.Rol rol = new YoResponse.Rol(sesion.rolNombre(), sesion.rolClave(), sesion.esPropietario());
		YoResponse.Plan plan = new YoResponse.Plan(sesion.planCodigo(), sesion.planNombre(),
				sesion.suscripcionEstado());
		return new YoResponse(persona, empresa, rol, plan, sesion.minutosInactividad(), sesion.funcionalidades(),
				sesion.permisos());
	}

	public List<EmpresaOpcionResponse> aEmpresas(List<EmpresaDisponible> empresas) {
		List<EmpresaOpcionResponse> respuesta = new ArrayList<>();
		for (EmpresaDisponible empresa : empresas) {
			respuesta.add(new EmpresaOpcionResponse(empresa.codigo(), empresa.razonSocial(), empresa.rol()));
		}
		return respuesta;
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
