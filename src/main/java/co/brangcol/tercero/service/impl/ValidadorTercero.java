package co.brangcol.tercero.service.impl;

import java.math.BigDecimal;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.brangcol.parametro.model.TipoDocumento;
import co.brangcol.parametro.service.BancoService;
import co.brangcol.parametro.service.MunicipioService;
import co.brangcol.parametro.service.TipoDocumentoService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.tercero.model.CondicionesCliente;
import co.brangcol.tercero.model.CondicionesProveedor;
import co.brangcol.tercero.model.DatosTercero;

// Mismas reglas que la base de datos, para responder con un mensaje claro antes de intentar guardar
@Component
public class ValidadorTercero {

	private static final Pattern DOCUMENTO = Pattern.compile("^[A-Za-z0-9-]{3,20}$");

	private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private static final Pattern TELEFONO = Pattern.compile("^\\+?[0-9 ()-]{7,20}$");

	private static final Pattern CUENTA = Pattern.compile("^[0-9-]{4,30}$");

	private static final Set<String> CONTROLES = Set.of("NINGUNO", "ADVERTIR", "BLOQUEAR");

	private static final Set<String> TIPOS_CUENTA = Set.of("AHORROS", "CORRIENTE", "BILLETERA");

	private final TipoDocumentoService tipoDocumentoService;

	private final MunicipioService municipioService;

	private final BancoService bancoService;

	public ValidadorTercero(TipoDocumentoService tipoDocumentoService, MunicipioService municipioService,
			BancoService bancoService) {
		this.tipoDocumentoService = tipoDocumentoService;
		this.municipioService = municipioService;
		this.bancoService = bancoService;
	}

	public void validar(DatosTercero d) {
		validarNombre(d);
		validarDocumento(d);
		validarContacto(d);
		if (d.cliente() == null && d.proveedor() == null) {
			fallar("Marque si es cliente, proveedor o ambos");
		}
		if (d.cliente() != null) {
			validarCliente(d.cliente());
		}
		if (d.proveedor() != null) {
			validarProveedor(d.proveedor());
		}
	}

	private void validarNombre(DatosTercero d) {
		if ("JURIDICA".equals(d.tipoPersona())) {
			exigir(d.razonSocial(), "Escriba la razón social");
			maximo(d.razonSocial(), 200, "La razón social es demasiado larga");
		}
		else if ("NATURAL".equals(d.tipoPersona())) {
			exigir(d.nombres(), "Escriba el nombre");
		}
		else {
			fallar("Indique si es persona natural o empresa");
		}
		maximo(d.nombres(), 100, "El nombre es demasiado largo");
		maximo(d.apellidos(), 100, "Los apellidos son demasiado largos");
		maximo(d.nombreComercial(), 200, "El nombre comercial es demasiado largo");
	}

	private void validarDocumento(DatosTercero d) {
		if (d.tipoDocumento() == null && d.numeroDocumento() == null) {
			return;
		}
		if (d.tipoDocumento() == null || d.numeroDocumento() == null) {
			fallar("Escriba el tipo y el número de documento");
		}
		TipoDocumento tipo = tipoDocumentoService.buscar(d.tipoDocumento());
		if (tipo == null) {
			fallar("El tipo de documento no es válido");
		}
		if ("JURIDICA".equals(d.tipoPersona()) && "NATURAL".equals(tipo.aplicaA())) {
			fallar("Una empresa se registra con NIT");
		}
		if (!DOCUMENTO.matcher(d.numeroDocumento()).matches()) {
			fallar("El número de documento solo admite letras, números y guiones (3 a 20)");
		}
	}

	private void validarContacto(DatosTercero d) {
		if (d.correo() != null && (d.correo().length() > 254 || !CORREO.matcher(d.correo()).matches())) {
			fallar("El correo no es válido");
		}
		if (d.telefono() != null && !TELEFONO.matcher(d.telefono()).matches()) {
			fallar("El teléfono no es válido");
		}
		maximo(d.direccion(), 200, "La dirección es demasiado larga");
		maximo(d.notas(), 1000, "Las notas son demasiado largas");
		if (d.municipio() != null) {
			municipioService.validarMunicipio(d.municipio());
		}
	}

	private void validarCliente(CondicionesCliente c) {
		plazo(c.plazoDias());
		if (c.cupoCredito() != null && c.cupoCredito().compareTo(BigDecimal.ZERO) < 0) {
			fallar("El cupo de crédito no puede ser negativo");
		}
		if (!CONTROLES.contains(c.controlCredito())) {
			fallar("El control de crédito no es válido");
		}
	}

	private void validarProveedor(CondicionesProveedor p) {
		plazo(p.plazoDias());
		boolean algunDato = p.banco() != null || p.tipoCuenta() != null || p.numeroCuenta() != null;
		if (!algunDato) {
			return;
		}
		if (p.banco() == null || p.tipoCuenta() == null || p.numeroCuenta() == null) {
			fallar("Para la cuenta bancaria escriba banco, tipo y número");
		}
		bancoService.validar(p.banco());
		if (!TIPOS_CUENTA.contains(p.tipoCuenta())) {
			fallar("El tipo de cuenta no es válido");
		}
		if (!CUENTA.matcher(p.numeroCuenta()).matches()) {
			fallar("El número de cuenta solo admite números y guiones");
		}
	}

	private void plazo(int dias) {
		if (dias < 0 || dias > 365) {
			fallar("El plazo debe estar entre 0 y 365 días");
		}
	}

	private void exigir(String valor, String mensaje) {
		if (valor == null) {
			fallar(mensaje);
		}
	}

	private void maximo(String valor, int largo, String mensaje) {
		if (valor != null && valor.length() > largo) {
			fallar(mensaje);
		}
	}

	private void fallar(String mensaje) {
		throw new NegocioException(HttpStatus.BAD_REQUEST, mensaje);
	}

}
