package co.brangcol.tercero.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import co.brangcol.parametro.model.TipoDocumento;
import co.brangcol.parametro.service.TipoDocumentoService;
import co.brangcol.tercero.service.PlantillaTercerosService;

@Service
public class PlantillaTercerosServiceImpl implements PlantillaTercerosService {

	private final TipoDocumentoService tipoDocumentoService;

	public PlantillaTercerosServiceImpl(TipoDocumentoService tipoDocumentoService) {
		this.tipoDocumentoService = tipoDocumentoService;
	}

	@Override
	public byte[] generar() {
		try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
			CellStyle titulo = libro.createCellStyle();
			Font negrita = libro.createFont();
			negrita.setBold(true);
			titulo.setFont(negrita);

			Sheet hoja = libro.createSheet("Clientes y proveedores");
			Row encabezado = hoja.createRow(0);
			Row ejemplo = hoja.createRow(1);
			for (int columna = 0; columna < ColumnasPlantilla.TITULOS.size(); columna++) {
				encabezado.createCell(columna).setCellValue(ColumnasPlantilla.TITULOS.get(columna));
				encabezado.getCell(columna).setCellStyle(titulo);
				ejemplo.createCell(columna).setCellValue(ColumnasPlantilla.EJEMPLO.get(columna));
				hoja.setColumnWidth(columna, 22 * 256);
			}

			Sheet tipos = libro.createSheet("Tipos de documento");
			Row tituloTipos = tipos.createRow(0);
			tituloTipos.createCell(0).setCellValue("Escriba");
			tituloTipos.createCell(1).setCellValue("Significa");
			tituloTipos.getCell(0).setCellStyle(titulo);
			tituloTipos.getCell(1).setCellStyle(titulo);
			int fila = 1;
			for (TipoDocumento tipo : tipoDocumentoService.todos()) {
				Row registro = tipos.createRow(fila);
				registro.createCell(0).setCellValue(tipo.abreviatura());
				registro.createCell(1).setCellValue(tipo.nombre());
				fila++;
			}
			tipos.setColumnWidth(1, 45 * 256);

			libro.write(salida);
			return salida.toByteArray();
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

}
