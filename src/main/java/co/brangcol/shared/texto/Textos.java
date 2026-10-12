package co.brangcol.shared.texto;

public final class Textos {

	private Textos() {
	}

	public static String vacioANulo(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}

	// Quita puntos y espacios que la gente suele escribir en cedulas y NIT (1.045.678.901)
	public static String documento(String valor) {
		String limpio = vacioANulo(valor);
		if (limpio == null) {
			return null;
		}
		return limpio.replace(".", "").replace(" ", "").toUpperCase();
	}

	public static String minusculas(String valor) {
		String limpio = vacioANulo(valor);
		return limpio == null ? null : limpio.toLowerCase();
	}

}
