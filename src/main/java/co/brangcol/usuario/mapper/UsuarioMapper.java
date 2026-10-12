package co.brangcol.usuario.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.brangcol.usuario.dto.UsuarioResponse;
import co.brangcol.usuario.model.Usuario;

@Component
public class UsuarioMapper {

	public UsuarioResponse aRespuesta(Usuario usuario) {
		return new UsuarioResponse(usuario.codigo(), usuario.nombres(), usuario.apellidos(), usuario.correo(),
				new UsuarioResponse.RolResumen(usuario.rolCodigo(), usuario.rolNombre()), usuario.esPropietario(),
				usuario.activo(), usuario.ultimoIngreso(), usuario.esYo());
	}

	public List<UsuarioResponse> aRespuestas(List<Usuario> usuarios) {
		List<UsuarioResponse> respuesta = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			respuesta.add(aRespuesta(usuario));
		}
		return respuesta;
	}

}
