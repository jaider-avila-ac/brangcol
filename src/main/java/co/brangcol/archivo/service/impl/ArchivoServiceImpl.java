package co.brangcol.archivo.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.brangcol.archivo.model.ArchivoLeido;
import co.brangcol.archivo.model.DatosArchivo;
import co.brangcol.archivo.repository.ArchivoRepository;
import co.brangcol.archivo.service.ArchivoService;
import co.brangcol.shared.error.NegocioException;

@Service
public class ArchivoServiceImpl implements ArchivoService {

	private final ArchivoRepository archivoRepository;

	private final AlmacenDisco almacen;

	public ArchivoServiceImpl(ArchivoRepository archivoRepository, AlmacenDisco almacen) {
		this.archivoRepository = archivoRepository;
		this.almacen = almacen;
	}

	@Override
	@Transactional(readOnly = true)
	public ArchivoLeido leer(String codigo) {
		DatosArchivo datos = archivoRepository.buscar(codigo);
		if (datos == null) {
			throw new NegocioException(HttpStatus.NOT_FOUND, "El archivo no existe");
		}
		return new ArchivoLeido(datos.nombreOriginal(), datos.tipoMime(), almacen.leer(datos.ruta()));
	}

}
