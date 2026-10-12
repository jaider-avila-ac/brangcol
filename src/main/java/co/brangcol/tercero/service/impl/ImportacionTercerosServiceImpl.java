package co.brangcol.tercero.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.shared.error.NegocioException;
import co.brangcol.shared.permiso.VerificadorPermisos;
import co.brangcol.tercero.model.Coincidencia;
import co.brangcol.tercero.model.DatosTercero;
import co.brangcol.tercero.model.ErrorFila;
import co.brangcol.tercero.model.FilaImportacion;
import co.brangcol.tercero.model.ResultadoImportacion;
import co.brangcol.tercero.service.ImportacionTercerosService;
import co.brangcol.tercero.service.TerceroService;

// Cada fila se valida antes de guardar; las filas con errores se informan y las demas se importan.
// Si el documento ya existe, solo se le agregan los roles que le falten (no se sobrescriben sus datos).
@Service
public class ImportacionTercerosServiceImpl implements ImportacionTercerosService {

	private final LectorExcelTerceros lector;

	private final ValidadorTercero validador;

	private final TerceroService terceroService;

	private final VerificadorPermisos permisos;

	public ImportacionTercerosServiceImpl(LectorExcelTerceros lector, ValidadorTercero validador,
			TerceroService terceroService, VerificadorPermisos permisos) {
		this.lector = lector;
		this.validador = validador;
		this.terceroService = terceroService;
		this.permisos = permisos;
	}

	@Override
	@Transactional
	public ResultadoImportacion importar(InputStream archivo) {
		permisos.exigir("terceros:tercero:importar");
		List<FilaImportacion> filas = lector.leer(archivo);
		List<ErrorFila> errores = new ArrayList<>();
		Set<String> documentos = new HashSet<>();
		int creados = 0;
		int actualizados = 0;
		for (FilaImportacion fila : filas) {
			if (fila.error() != null) {
				errores.add(new ErrorFila(fila.fila(), fila.error()));
				continue;
			}
			DatosTercero datos = fila.datos();
			String error = validar(datos, documentos);
			if (error != null) {
				errores.add(new ErrorFila(fila.fila(), error));
				continue;
			}
			Coincidencia existente = terceroService.buscarPorDocumento(datos.tipoDocumento(), datos.numeroDocumento());
			if (existente == null) {
				terceroService.crear(datos);
				creados++;
			}
			else if (terceroService.agregarRoles(existente.codigo(), datos)) {
				actualizados++;
			}
			else {
				errores.add(new ErrorFila(fila.fila(), existente.nombre() + " ya estaba registrado"));
			}
		}
		return new ResultadoImportacion(creados, actualizados, errores);
	}

	private String validar(DatosTercero datos, Set<String> documentos) {
		try {
			validador.validar(datos);
		}
		catch (NegocioException ex) {
			return ex.getMessage();
		}
		if (datos.numeroDocumento() == null) {
			return "Falta el número de documento";
		}
		if (!documentos.add(datos.tipoDocumento() + ":" + datos.numeroDocumento())) {
			return "El documento está repetido en el archivo";
		}
		return null;
	}

}
