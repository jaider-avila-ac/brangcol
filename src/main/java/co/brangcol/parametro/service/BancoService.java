package co.brangcol.parametro.service;

import java.util.List;

public interface BancoService {

	List<String> nombres();

	void validar(String nombre);

}
