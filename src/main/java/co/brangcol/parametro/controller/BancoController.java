package co.brangcol.parametro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.brangcol.parametro.service.BancoService;

@RestController
@RequestMapping("/api/v1/parametros/bancos")
public class BancoController {

	private final BancoService bancoService;

	public BancoController(BancoService bancoService) {
		this.bancoService = bancoService;
	}

	@GetMapping
	public List<String> listar() {
		return bancoService.nombres();
	}

}
