package co.brangcol.shared.error;

import org.springframework.http.HttpStatus;

public record ErrorTraducido(HttpStatus estado, String mensaje) {
}
