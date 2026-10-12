package co.brangcol.usuario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.usuario.dto.RolResponse;
import co.brangcol.usuario.model.PermisoRol;
import co.brangcol.usuario.model.Rol;

@Component
public class RolMapper {

	public List<RolResponse> aRespuestas(List<Rol> roles) {
		List<RolResponse> respuesta = new ArrayList<>();
		for (Rol rol : roles) {
			List<RolResponse.Permiso> permisos = new ArrayList<>();
			for (PermisoRol permiso : rol.permisos()) {
				permisos.add(new RolResponse.Permiso(permiso.codigo(), permiso.descripcion()));
			}
			respuesta.add(new RolResponse(rol.codigo(), rol.nombre(), rol.descripcion(), rol.clave(), permisos));
		}
		return respuesta;
	}

}
