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
