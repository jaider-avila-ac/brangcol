package co.brangcol.shared.error;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/prueba-errores")
class ErrorPruebaController {

	@GetMapping("/base/{codigo}")
	void base(@PathVariable String codigo) {
		ServerErrorMessage mensaje = new ServerErrorMessage("SERROR\0C" + codigo + "\0MMensaje desde la base\0");
		throw new IllegalStateException("envuelta", new PSQLException(mensaje));
	}

	@GetMapping("/inesperado")
	void inesperado() {
		throw new IllegalStateException("fallo interno");
	}

	@PostMapping("/validacion")
	void validacion(@Valid @RequestBody Cuerpo cuerpo) {
	}

	record Cuerpo(@NotBlank(message = "El nombre es obligatorio") String nombre) {
	}

}
