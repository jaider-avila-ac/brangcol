package co.brangcol.tercero.service.impl;

import java.util.List;

// Orden de las columnas del archivo de importacion; la plantilla y el lector usan la misma lista
final class ColumnasPlantilla {

	static final int TIPO_DOCUMENTO = 0;
	static final int NUMERO_DOCUMENTO = 1;
	static final int NOMBRES = 2;
	static final int APELLIDOS = 3;
	static final int RAZON_SOCIAL = 4;
	static final int NOMBRE_COMERCIAL = 5;
	static final int TELEFONO = 6;
	static final int CORREO = 7;
	static final int DIRECCION = 8;
	static final int MUNICIPIO = 9;
	static final int ES_CLIENTE = 10;
	static final int ES_PROVEEDOR = 11;
	static final int PLAZO_DIAS = 12;

	static final List<String> TITULOS = List.of("Tipo de documento", "Número de documento", "Nombres", "Apellidos",
			"Razón social (si es empresa)", "Nombre comercial", "Teléfono", "Correo", "Dirección", "Municipio",
			"Es cliente (SI/NO)", "Es proveedor (SI/NO)", "Plazo en días");

	static final List<String> EJEMPLO = List.of("CC", "1045678901", "Juan", "Pérez", "", "", "3001234567",
			"juan@correo.com", "Calle 10 # 20-30", "Barranquilla", "SI", "NO", "0");

	static final int MAXIMO_FILAS = 5000;

	private ColumnasPlantilla() {
	}

}
