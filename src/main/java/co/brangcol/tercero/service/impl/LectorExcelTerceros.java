package co.brangcol.tercero.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.brangcol.parametro.model.Municipio;
import co.brangcol.parametro.model.TipoDocumento;
import co.brangcol.parametro.service.MunicipioService;
import co.brangcol.parametro.service.TipoDocumentoService;
import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.texto.Textos;
import co.brangcol.tercero.model.CondicionesCliente;
import co.brangcol.tercero.model.CondicionesProveedor;
import co.brangcol.tercero.model.DatosTercero;
import co.brangcol.tercero.model.FilaImportacion;

// Convierte cada fila del Excel en datos de tercero; los errores de lectura quedan en la fila
@Component
public class LectorExcelTerceros {

	private final TipoDocumentoService tipoDocumentoService;

	private final MunicipioService municipioService;

	private final DataFormatter formato = new DataFormatter();

	public LectorExcelTerceros(TipoDocumentoService tipoDocumentoService, MunicipioService municipioService) {
		this.tipoDocumentoService = tipoDocumentoService;
		this.municipioService = municipioService;
	}

	public List<FilaImportacion> leer(InputStream archivo) {
		Map<String, String> municipios = municipiosPorNombre();
		Map<String, TipoDocumento> tipos = tiposPorTexto();
		List<FilaImportacion> filas = new ArrayList<>();
		try (Workbook libro = WorkbookFactory.create(archivo)) {
			Sheet hoja = libro.getSheetAt(0);
			if (hoja.getLastRowNum() > ColumnasPlantilla.MAXIMO_FILAS) {
				throw new NegocioException(HttpStatus.BAD_REQUEST,
						"El archivo tiene más de " + ColumnasPlantilla.MAXIMO_FILAS + " filas");
			}
			for (int numero = 1; numero <= hoja.getLastRowNum(); numero++) {
				Row fila = hoja.getRow(numero);
				if (fila == null || filaVacia(fila)) {
					continue;
				}
				filas.add(leerFila(fila, numero + 1, municipios, tipos));
			}
		}
		catch (IOException | RuntimeException ex) {
			if (ex instanceof NegocioException negocio) {
				throw negocio;
			}
			throw new NegocioException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo. Use la plantilla en formato Excel (.xlsx)");
		}
		return filas;
	}

	private FilaImportacion leerFila(Row fila, int numero, Map<String, String> municipios,
			Map<String, TipoDocumento> tipos) {
		String tipoTexto = texto(fila, ColumnasPlantilla.TIPO_DOCUMENTO);
		TipoDocumento tipo = tipoTexto == null ? null : tipos.get(tipoTexto.toUpperCase());
		if (tipoTexto != null && tipo == null) {
			return new FilaImportacion(numero, null, "Tipo de documento \"" + tipoTexto + "\" no reconocido");
		}
		String municipioTexto = texto(fila, ColumnasPlantilla.MUNICIPIO);
		String municipio = null;
		if (municipioTexto != null) {
			municipio = municipios.get(clave(municipioTexto));
		}
		Integer plazo = entero(texto(fila, ColumnasPlantilla.PLAZO_DIAS));
		if (plazo == null) {
			return new FilaImportacion(numero, null, "El plazo debe ser un número de días");
		}
		boolean esCliente = siNo(texto(fila, ColumnasPlantilla.ES_CLIENTE));
		boolean esProveedor = siNo(texto(fila, ColumnasPlantilla.ES_PROVEEDOR));
		if (!esCliente && !esProveedor) {
			esCliente = true;
		}
		String razonSocial = texto(fila, ColumnasPlantilla.RAZON_SOCIAL);
		DatosTercero datos = new DatosTercero(razonSocial == null ? "NATURAL" : "JURIDICA",
				tipo == null ? null : tipo.codigo(), Textos.documento(texto(fila, ColumnasPlantilla.NUMERO_DOCUMENTO)),
				razonSocial, texto(fila, ColumnasPlantilla.NOMBRES), texto(fila, ColumnasPlantilla.APELLIDOS),
				texto(fila, ColumnasPlantilla.NOMBRE_COMERCIAL), false, texto(fila, ColumnasPlantilla.DIRECCION),
				municipio, Textos.minusculas(texto(fila, ColumnasPlantilla.CORREO)),
				texto(fila, ColumnasPlantilla.TELEFONO), null,
				esCliente ? new CondicionesCliente(plazo, null, "NINGUNO") : null,
				esProveedor ? new CondicionesProveedor(plazo, null, null, null) : null);
		return new FilaImportacion(numero, datos, null);
	}

	private Map<String, TipoDocumento> tiposPorTexto() {
		Map<String, TipoDocumento> mapa = new HashMap<>();
		for (TipoDocumento tipo : tipoDocumentoService.todos()) {
			mapa.put(tipo.codigo(), tipo);
			mapa.put(tipo.abreviatura().toUpperCase(), tipo);
		}
		return mapa;
	}

	private Map<String, String> municipiosPorNombre() {
		Map<String, String> mapa = new HashMap<>();
		for (Municipio municipio : municipioService.todos()) {
			mapa.put(clave(municipio.nombre()), municipio.codigo());
			mapa.put(municipio.codigo(), municipio.codigo());
		}
		return mapa;
	}

	private String clave(String texto) {
		String sinTildes = Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD);
		return sinTildes.replaceAll("\\p{M}", "");
	}

	private String texto(Row fila, int columna) {
		Cell celda = fila.getCell(columna);
		if (celda == null) {
			return null;
		}
		return Textos.vacioANulo(formato.formatCellValue(celda));
	}

	private boolean filaVacia(Row fila) {
		for (int columna = 0; columna < ColumnasPlantilla.TITULOS.size(); columna++) {
			if (texto(fila, columna) != null) {
				return false;
			}
		}
		return true;
	}

	private boolean siNo(String valor) {
		if (valor == null) {
			return false;
		}
		String limpio = clave(valor);
		return limpio.equals("si") || limpio.equals("s") || limpio.equals("x") || limpio.equals("1");
	}

	private Integer entero(String valor) {
		if (valor == null) {
			return 0;
		}
		try {
			return new BigDecimal(valor.replace(",", ".")).intValueExact();
		}
		catch (ArithmeticException | NumberFormatException ex) {
			return null;
		}
	}

}
