# BrangCol - Backend

API de BrangCol en Spring Boot 4 y Java 21.

La base de datos vive en su propio repositorio (`brangol_basedatos`) y su propio stack. Este proyecto solo se conecta con el usuario `brangcol_app` y no aplica migraciones.

## Desarrollo

```bash
cp .env.example .env          # DB_PASSWORD = DB_APP_PASSWORD de brangol_basedatos/.env
./mvnw spring-boot:run
```

- API: `http://localhost:8080`
- Salud y metricas (puerto interno): `http://localhost:8081/actuator/health` y `/actuator/prometheus`

Con Docker:

```bash
docker compose up -d --build                            # solo el backend
docker compose --profile observabilidad up -d --build   # backend con Prometheus, Grafana y Loki
```

Grafana queda en `http://localhost:3000` (usuario `admin`, clave `GRAFANA_PASSWORD`) con el tablero "BrangCol API".

## Pruebas

```bash
./mvnw test
```

Las pruebas levantan un PostgreSQL temporal con Testcontainers y le aplican las migraciones de `../brangol_basedatos/migraciones`. Otra ruta se indica con `-Dbrangcol.migraciones=/ruta/migraciones`.

## Sesion y multiempresa

- El token de sesion llega en la cookie `bc_sesion`.
- `SesionTransactionManager` llama a `plataforma.usar_sesion(token)` al iniciar cada transaccion. La base valida el token y fija la empresa y la persona; el backend nunca envia `empresa_id`.
- Toda consulta debe ir dentro de una transaccion (`@Transactional`); sin ella la base no devuelve datos de ninguna empresa.

## Errores

Todas las respuestas de error tienen la forma `{ "message": "..." }`. Los errores de negocio de la base (`SQLSTATE BCxxx`) se traducen a 401, 403, 404, 409, 422 o 429. Cada respuesta trae la cabecera `X-Trace-Id` para buscar la peticion en los logs.

## Variables

| Variable | Uso |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexion a la base |
| `SENTRY_DSN` | Errores en Sentry (vacio lo desactiva) |
| `ENTORNO` | Nombre del entorno en Sentry |
| `LOG_FORMATO` | `ecs` (JSON) por defecto |
| `GRAFANA_PASSWORD` | Clave del administrador de Grafana |
| `COOKIE_SEGURA` | `true` por defecto; la cookie solo viaja por HTTPS (localhost se acepta) |
| `FORWARD_HEADERS` | `none` por defecto; `native` detras de un proxy |
| `FRONT_URL` | Direccion del front, para los enlaces de los correos |
| `RESEND_API_KEY`, `CORREO_REMITENTE` | Envio de correos con Resend (vacio no envia) |

## Autenticacion (`/api/v1/auth`)

| Metodo | Ruta | Uso |
|---|---|---|
| POST | `/login` | `{correo, contrasena, empresa?}`. Responde `OK` (crea la cookie) o `ELEGIR_EMPRESA` |
| POST | `/empresas` | Empresas a las que puede entrar (exige la contrasena) |
| POST | `/salir` | Cierra la sesion y borra la cookie |
| GET | `/yo` | Persona, empresa, rol, plan, funcionalidades y permisos |
| GET | `/sesiones` | Mis sesiones abiertas |
| DELETE | `/sesiones/{codigo}` | Cierra una sesion |
| DELETE | `/sesiones` | Cierra todas las demas |
| PUT | `/contrasena` | `{actual, nueva}`. Cierra las otras sesiones |

Los intentos fallidos y el bloqueo (5 intentos por cuenta, 20 por IP en 10 minutos) los lleva la base. Detras de un proxy (Traefik, Nginx) se usa `FORWARD_HEADERS=native` para registrar la IP real.

## Empresa, usuarios y roles

| Metodo | Ruta | Uso |
|---|---|---|
| GET, PUT | `/api/v1/empresa` | Datos de la empresa (el NIT y el estado no se editan) |
| GET, PUT | `/api/v1/empresa/configuracion` | Decimales, inventario, aplicacion de pagos, sesiones |
| GET, POST, PUT | `/api/v1/empresa/sedes` | Sedes |
| GET | `/api/v1/parametros/municipios?buscar=` | Buscador de municipios |
| GET | `/api/v1/usuarios` | Usuarios de la empresa |
| PUT | `/api/v1/usuarios/{codigo}/rol`, `/estado` | Cambiar rol, activar o desactivar |
| GET | `/api/v1/roles` | Roles con sus permisos |
| GET, POST, DELETE | `/api/v1/usuarios/invitaciones` | Invitaciones pendientes, invitar y anular |
| POST | `/api/v1/invitaciones/consultar`, `/aceptar` | Publicas: pantalla "Aceptar invitacion" |

Las invitaciones se envian por correo con Resend si `RESEND_API_KEY` esta configurada; si no, la respuesta trae el enlace para compartirlo. El token va despues de `#` en el enlace para que nunca llegue al servidor ni a los logs.

## Clientes y proveedores (`/api/v1/terceros`)

| Metodo | Ruta | Uso |
|---|---|---|
| GET | `/terceros?texto=&rol=TODOS\|CLIENTES\|PROVEEDORES&soloDeben=&pagina=&tamano=` | Busqueda paginada, tolera errores de escritura y tildes |
| GET | `/terceros/documento?tipo=&numero=` | Si ya existe alguien con ese documento (204 si no) |
| GET, POST, PUT | `/terceros`, `/terceros/{codigo}` | Ver, crear y editar (un mismo NIT puede ser cliente y proveedor) |
| PUT | `/terceros/{codigo}/estado` | Activar o desactivar (no se borran) |
| GET | `/terceros/plantilla` | Plantilla de Excel para importar |
| POST | `/terceros/importar` | Importa un .xlsx (campo `archivo`); devuelve creados, actualizados y errores por fila |
| GET | `/parametros/tipos-documento`, `/parametros/bancos` | Catalogos |

## Productos (`/api/v1/productos`)

| Metodo | Ruta | Uso |
|---|---|---|
| GET | `/productos?texto=&tipo=&categoria=&soloComponentes=&incluirInactivos=&pagina=&tamano=` | Busqueda paginada por nombre, codigo o codigo de barras; trae precio con impuesto |
| GET, POST, PUT | `/productos`, `/productos/{codigo}` | Ver, crear y editar (SKU automatico P00001 si se deja vacio, codigos de barras, presentaciones) |
| PUT | `/productos/{codigo}/estado` | Activar o desactivar (no se borran) |
| GET | `/productos/recetas?texto=` | Servicios, combos y fabricados con lo que lleva cada uno, costo y precio |
| GET, PUT | `/productos/{codigo}/receta` | Insumos que gasta un servicio, combo o fabricado; si la version ya se uso se crea una nueva |
| GET, POST, PUT | `/productos/categorias`, `/productos/marcas` | Catalogos de la empresa |
| GET | `/parametros/unidades`, `/impuestos` | Unidades de medida e impuestos |

## Archivos

| Metodo | Ruta | Uso |
|---|---|---|
| GET | `/archivos/{codigo}` | Devuelve una imagen de la empresa de la sesion |
| POST | `/empresa/logo`, `/auth/foto` | Logo de la empresa y foto del usuario |

Los archivos se guardan en disco en `ARCHIVOS_DIR` (por defecto `./datos/archivos`).

## Inventario (`/api/v1/inventario`)

| Metodo | Ruta | Uso |
|---|---|---|
| GET, POST, PUT | `/inventario/bodegas`, `/inventario/bodegas/{codigo}` | Bodegas (limite del plan; la principal no se desactiva) |
| GET | `/inventario/existencias?texto=&bodega=&soloBajoMinimo=&pagina=&tamano=` | Cuanto hay de cada producto (sin bodega suma todas), costo promedio y valor |
| GET | `/inventario/existencias/resumen` | Valor del inventario, productos y cuantos estan por acabarse |
| PUT | `/inventario/existencias/minimo` | Minimo de un producto en una bodega |
| GET | `/inventario/kardex/{item}?bodega=&desde=&hasta=` | Movimientos del producto con el saldo que quedo |
| GET, POST | `/inventario/ajustes`, `/inventario/ajustes/{codigo}` | Inventario inicial, entradas y salidas, consumo interno (se aplican al registrarse) |
| POST | `/inventario/ajustes/{codigo}/anular` | Anula y devuelve las cantidades (`{motivo}`) |
| GET, POST | `/inventario/traslados`, `/inventario/traslados/{codigo}` | Traslados entre bodegas (salen al registrarse) |
| POST | `/inventario/traslados/{codigo}/recibir`, `/anular` | Confirma lo que llego (sin lineas: todo) o anula si sigue en camino |
| POST | `/inventario/conteos` | Toma fisica: corrige las diferencias con un ajuste (`{ajuste}` o null si todo cuadra) |

Las cantidades van siempre en la unidad base del producto. Las existencias solo cambian por movimientos del kardex.
