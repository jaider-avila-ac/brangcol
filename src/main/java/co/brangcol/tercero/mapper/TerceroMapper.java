package co.brangcol.tercero.mapper;

import org.springframework.stereotype.Component;

import co.brangcol.shared.texto.Textos;
import co.brangcol.tercero.dto.CondicionesClienteDto;
import co.brangcol.tercero.dto.CondicionesProveedorDto;
import co.brangcol.tercero.dto.TerceroRequest;
import co.brangcol.tercero.dto.TerceroResponse;
import co.brangcol.tercero.model.CondicionesCliente;
import co.brangcol.tercero.model.CondicionesProveedor;
import co.brangcol.tercero.model.DatosTercero;
import co.brangcol.tercero.model.Tercero;

@Component
public class TerceroMapper {

	public DatosTercero aDatos(TerceroRequest r) {
		boolean juridica = "JURIDICA".equals(r.tipoPersona());
		return new DatosTercero(r.tipoPersona(), Textos.vacioANulo(r.tipoDocumento()),
				Textos.documento(r.numeroDocumento()), juridica ? Textos.vacioANulo(r.razonSocial()) : null,
				juridica ? null : Textos.vacioANulo(r.nombres()), juridica ? null : Textos.vacioANulo(r.apellidos()),
				Textos.vacioANulo(r.nombreComercial()), Boolean.TRUE.equals(r.responsableIva()), Textos.vacioANulo(r.direccion()),
				Textos.vacioANulo(r.municipio()), Textos.minusculas(r.correo()), Textos.vacioANulo(r.telefono()),
				Textos.vacioANulo(r.notas()), aCliente(r.cliente()), aProveedor(r.proveedor()));
	}

	public TerceroResponse aRespuesta(Tercero t) {
		TerceroResponse.Municipio municipio = null;
		if (t.municipioCodigo() != null) {
			municipio = new TerceroResponse.Municipio(t.municipioCodigo(), t.municipioNombre());
		}
		CondicionesClienteDto cliente = null;
		if (t.cliente() != null) {
			cliente = new CondicionesClienteDto(t.cliente().plazoDias(), t.cliente().cupoCredito(),
					t.cliente().controlCredito());
		}
		CondicionesProveedorDto proveedor = null;
		if (t.proveedor() != null) {
			proveedor = new CondicionesProveedorDto(t.proveedor().plazoDias(), t.proveedor().banco(),
					t.proveedor().tipoCuenta(), t.proveedor().numeroCuenta());
		}
		return new TerceroResponse(t.codigo(), t.tipoPersona(), t.tipoDocumento(), t.numeroDocumento(), t.dv(),
				t.razonSocial(), t.nombres(), t.apellidos(), t.nombreComercial(), t.nombreCompleto(),
				t.responsableIva(), t.direccion(), municipio, t.correo(), t.telefono(), t.notas(), t.activo(),
				"COMPLETO".equals(t.estadoDatos()), cliente, proveedor, t.debe(), t.saldoFavor(), t.leDebemos());
	}

	private CondicionesCliente aCliente(CondicionesClienteDto dto) {
		if (dto == null) {
			return null;
		}
		String control = dto.controlCredito() == null ? "NINGUNO" : dto.controlCredito();
		return new CondicionesCliente(dias(dto.plazoDias()), dto.cupoCredito(), control);
	}

	private CondicionesProveedor aProveedor(CondicionesProveedorDto dto) {
		if (dto == null) {
			return null;
		}
		return new CondicionesProveedor(dias(dto.plazoDias()), Textos.vacioANulo(dto.banco()),
				Textos.vacioANulo(dto.tipoCuenta()), Textos.vacioANulo(dto.numeroCuenta()));
	}

	private int dias(Integer plazo) {
		return plazo == null ? 0 : plazo;
	}

}
