package co.brangcol.empresa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.brangcol.archivo.model.ImagenGuardada;
import co.brangcol.archivo.service.ImagenService;
import co.brangcol.empresa.repository.LogoRepository;
import co.brangcol.empresa.service.LogoService;

@Service
public class LogoServiceImpl implements LogoService {

	private final ImagenService imagenService;

	private final LogoRepository logoRepository;

	public LogoServiceImpl(ImagenService imagenService, LogoRepository logoRepository) {
		this.imagenService = imagenService;
		this.logoRepository = logoRepository;
	}

	@Override
	@Transactional
	public String subir(MultipartFile archivo) {
		ImagenGuardada imagen = imagenService.guardar(archivo);
		try {
			return logoRepository.registrar(imagen);
		}
		catch (RuntimeException ex) {
			imagenService.descartar(imagen);
			throw ex;
		}
	}

}
