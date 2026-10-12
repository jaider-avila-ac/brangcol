package co.brangcol.usuario.model;

import java.util.List;

public record Rol(String codigo, String nombre, String descripcion, String clave, List<PermisoRol> permisos) {
}
