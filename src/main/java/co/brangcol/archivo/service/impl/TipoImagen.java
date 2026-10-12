package co.brangcol.archivo.service.impl;

// Reconoce la imagen por sus primeros bytes, no por el nombre ni por lo que diga el navegador
enum TipoImagen {

	PNG("image/png", "png"), JPEG("image/jpeg", "jpg"), WEBP("image/webp", "webp");

	private final String mime;

	private final String extension;

	TipoImagen(String mime, String extension) {
		this.mime = mime;
		this.extension = extension;
	}

	String mime() {
		return mime;
	}

	String extension() {
		return extension;
	}

	static TipoImagen detectar(byte[] b) {
		if (b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
			return PNG;
		}
		if (b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
			return JPEG;
		}
		if (b.length > 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F' && b[8] == 'W' && b[9] == 'E'
				&& b[10] == 'B' && b[11] == 'P') {
			return WEBP;
		}
		return null;
	}

}
