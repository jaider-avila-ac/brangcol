package co.brangcol.shared.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.sentry.Sentry;

@RestControllerAdvice
public class ManejadorErrores {

	private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

	private final TraductorErrorBaseDatos traductor;

	public ManejadorErrores(TraductorErrorBaseDatos traductor) {
		this.traductor = traductor;
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<ErrorRespuesta> negocio(NegocioException ex) {
		return respuesta(ex.getEstado(), ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException ex) {
		FieldError campo = ex.getBindingResult().getFieldError();
		if (campo == null || campo.getDefaultMessage() == null) {
			return respuesta(HttpStatus.BAD_REQUEST, "Hay datos inválidos o incompletos");
		}
		return respuesta(HttpStatus.BAD_REQUEST, campo.getDefaultMessage());
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	public ResponseEntity<ErrorRespuesta> peticionInvalida(Exception ex) {
		return respuesta(HttpStatus.BAD_REQUEST, "La petición no tiene un formato válido");
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorRespuesta> noEncontrado(NoResourceFoundException ex) {
		return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado");
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorRespuesta> metodo(HttpRequestMethodNotSupportedException ex) {
		return respuesta(HttpStatus.METHOD_NOT_ALLOWED, "Operación no permitida");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorRespuesta> general(Exception ex) {
		ErrorTraducido traducido = traductor.traducir(ex);
		if (traducido != null) {
			return respuesta(traducido.estado(), traducido.mensaje());
		}
		log.error("Error no controlado", ex);
		Sentry.captureException(ex);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocurrió un error inesperado. Intente de nuevo o informe al administrador.");
	}

	private ResponseEntity<ErrorRespuesta> respuesta(HttpStatus estado, String mensaje) {
		return ResponseEntity.status(estado).body(new ErrorRespuesta(mensaje));
	}

}
