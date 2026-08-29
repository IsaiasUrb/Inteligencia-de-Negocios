# Dulce Caña · CRUD (Spring Boot)

Aplicación Spring Boot para gestionar las tablas operativas de `dulcecana_db`
(esquema `public`): **proveedor, lote_produccion, producto, cliente, pedido,
detalle_pedido**. Incluye API REST (JSON) y una interfaz web simple con
Thymeleaf para crear, ver, editar y eliminar registros desde el navegador.

> No toca las tablas del data warehouse (`dw_dulce_cana.*`) — esas son de
> solo reportes/analítica, generadas por ETL, no se editan a mano.

## Requisitos

- **Java 17 o superior** (`java -version`)
- **Maven** (`mvn -version`) — o usa tu IDE (IntelliJ/Eclipse) que ya trae Maven integrado
- **PostgreSQL corriendo** en `localhost:5432`, con la base `dulcecana_db` ya restaurada
  (el mismo servidor que usa pgAdmin y el conector de Claude Desktop)

## Configuración de la base de datos

Ya está configurada en `src/main/resources/application.properties` con los
mismos datos que usaste en pgAdmin:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/dulcecana_db
spring.datasource.username=postgres
spring.datasource.password=admin
```

Si tu contraseña o puerto son distintos, ajústalos ahí.

**Importante:** `spring.jpa.hibernate.ddl-auto=validate` — la app NUNCA crea, altera
ni borra tablas. Solo verifica al arrancar que las entidades coincidan con las
tablas que ya existen. Si la base no está corriendo o el esquema no coincide,
la aplicación no arrancará (mejor eso a que te borre datos por accidente).

## Cómo ejecutarla

Desde la carpeta del proyecto:

```bash
mvn spring-boot:run
```

(o en Windows: `mvnw.cmd spring-boot:run` si tienes el wrapper, o simplemente
ábrelo en IntelliJ/Eclipse como proyecto Maven y ejecuta `CrudApplication`).

La primera vez descargará las dependencias de internet — puede tardar un
par de minutos.

Cuando arranque, abre en el navegador:

```
http://localhost:8080
```

Ahí verás el panel con enlaces a cada tabla (Proveedores, Lotes de producción,
Productos, Clientes, Pedidos, Detalle de pedidos) — cada una con su lista,
formulario de creación/edición y botón de eliminar.

## API REST

Cada entidad también tiene endpoints JSON bajo `/api/...`, útiles para probar
con Postman/Insomnia o conectar otro frontend:

| Recurso | Base URL |
|---|---|
| Proveedores | `GET/POST /api/proveedores`, `GET/PUT/DELETE /api/proveedores/{id}` |
| Lotes de producción | `GET/POST /api/lotes`, `GET/PUT/DELETE /api/lotes/{id}` |
| Productos | `GET/POST /api/productos`, `GET/PUT/DELETE /api/productos/{id}` |
| Clientes | `GET/POST /api/clientes`, `GET/PUT/DELETE /api/clientes/{id}` |
| Pedidos | `GET/POST /api/pedidos`, `GET/PUT/DELETE /api/pedidos/{id}` |
| Detalle de pedidos | `GET/POST /api/detalles-pedido`, `GET/PUT/DELETE /api/detalles-pedido/{id}` |

Ejemplo con `curl`:

```bash
curl http://localhost:8080/api/productos
```

## Estructura del proyecto

```
src/main/java/com/dulcecana/crud/
  entity/          -> Proveedor, LoteProduccion, Cliente, Producto, Pedido, DetallePedido
  repository/      -> interfaces Spring Data JPA (una por entidad)
  controller/rest/ -> endpoints JSON (/api/...)
  controller/web/  -> controladores que renderizan las paginas Thymeleaf
  config/          -> conversores para que los <select> de proveedor/cliente/etc.
                      en los formularios web funcionen correctamente
src/main/resources/
  application.properties
  templates/       -> paginas Thymeleaf (una carpeta por entidad: list.html + form.html)
  static/css/      -> estilos
```

## Nota honesta sobre esta entrega

Este proyecto se generó y se revisó cuidadosamente a mano (nombres de columnas,
relaciones, tipos de datos), pero el entorno donde lo escribí tiene bloqueado
el acceso a Maven Central, así que **no pude compilarlo aquí para confirmar
que compile sin errores** antes de enviártelo. Si al ejecutar `mvn spring-boot:run`
(o compilar en tu IDE) te sale algún error, pégamelo tal cual y lo corregimos
de inmediato.
