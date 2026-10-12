package co.brangcol.empresa.model;

public record ConfiguracionEmpresa(int decimalesCantidad, int decimalesPrecio, boolean controlaInventario,
		boolean permiteInventarioNegativo, int maxSesionesPorUsuario, int minutosInactividad,
		String aplicacionPagos) {
}
