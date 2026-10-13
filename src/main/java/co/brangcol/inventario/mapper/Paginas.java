package co.brangcol.inventario.mapper;

import java.util.List;

import co.brangcol.shared.web.PageResponse;

final class Paginas {

	private Paginas() {
	}

	static <T> PageResponse<T> de(List<T> contenido, long total, int pagina, int tamano) {
		int paginas = (int) ((total + tamano - 1) / tamano);
		return new PageResponse<>(contenido, pagina, tamano, total, paginas);
	}

}
