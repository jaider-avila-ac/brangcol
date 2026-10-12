package co.brangcol.shared.sesion;

public final class SesionActual {

	private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

	private SesionActual() {
	}

	public static void establecer(String token) {
		TOKEN.set(token);
	}

	public static String token() {
		return TOKEN.get();
	}

	public static void limpiar() {
		TOKEN.remove();
	}

}
