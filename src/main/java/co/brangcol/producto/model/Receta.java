package co.brangcol.producto.model;

import java.util.List;

public record Receta(int version, boolean bloqueada, List<ComponenteReceta> componentes) {
}
