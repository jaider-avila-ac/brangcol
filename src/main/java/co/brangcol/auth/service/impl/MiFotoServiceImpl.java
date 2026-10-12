package co.brangcol.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.model.ImagenGuardada;
import co.brangcol.archivo.service.ImagenService;
import co.brangcol.auth.repository.MiFotoRepository;
import co.brangcol.auth.service.MiFotoService;

@Service
public class MiFotoServiceImpl implements MiFotoService {

	private final ImagenService imagenService;

	private final MiFotoRepository miFotoRepository;

	public MiFotoServiceImpl(ImagenService imagenService, MiFotoRepository miFotoRepository) {
		this.imagenService = imagenService;
		this.miFotoRepository = miFotoRepository;
	}

	@Override
	@Transactional
	public String subir(MultipartFile archivo) {
		ImagenGuardada imagen = imagenService.guardar(archivo);
		try {
			return miFotoRepository.registrar(imagen);
		}
		catch (RuntimeException ex) {
			imagenService.descartar(imagen);
			throw ex;
		}
	}

}
