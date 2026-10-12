package co.brangcol.empresa.mapper;

final class Textos {

	private Textos() {
	}

	static String vacioANulo(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}

}
