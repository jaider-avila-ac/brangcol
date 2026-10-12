package co.brangcol.producto.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.model.ImagenGuardada;
import co.brangcol.archivo.service.ImagenService;
import co.brangcol.producto.repository.FotoProductoRepository;
import co.brangcol.producto.service.FotoProductoService;

@Service
public class FotoProductoServiceImpl implements FotoProductoService {

	private final ImagenService imagenService;

	private final FotoProductoRepository fotoProductoRepository;

	public FotoProductoServiceImpl(ImagenService imagenService, FotoProductoRepository fotoProductoRepository) {
		this.imagenService = imagenService;
		this.fotoProductoRepository = fotoProductoRepository;
	}

	@Override
	@Transactional
	public String subir(String itemCodigo, MultipartFile archivo) {
		ImagenGuardada imagen = imagenService.guardar(archivo);
		try {
			return fotoProductoRepository.registrar(itemCodigo, imagen);
		}
		catch (RuntimeException ex) {
			imagenService.descartar(imagen);
			throw ex;
		}
	}

}
