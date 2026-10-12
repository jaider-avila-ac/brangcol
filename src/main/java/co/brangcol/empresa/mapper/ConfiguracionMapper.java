package co.brangcol.empresa.mapper;

import org.springframework.stereotype.Component;

import co.brangcol.empresa.dto.ConfiguracionDto;
import co.brangcol.empresa.model.ConfiguracionEmpresa;

@Component
public class ConfiguracionMapper {

	public ConfiguracionDto aDto(ConfiguracionEmpresa configuracion) {
		return new ConfiguracionDto(configuracion.decimalesCantidad(), configuracion.decimalesPrecio(),
				configuracion.controlaInventario(), configuracion.permiteInventarioNegativo(),
				configuracion.maxSesionesPorUsuario(), configuracion.minutosInactividad(),
				configuracion.aplicacionPagos());
	}

	public ConfiguracionEmpresa aModelo(ConfiguracionDto dto) {
		return new ConfiguracionEmpresa(dto.decimalesCantidad(), dto.decimalesPrecio(), dto.controlaInventario(),
				dto.permiteInventarioNegativo(), dto.maxSesionesPorUsuario(), dto.minutosInactividad(),
				dto.aplicacionPagos());
	}

}
