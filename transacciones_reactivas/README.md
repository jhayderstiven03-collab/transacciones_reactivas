# Transacciones Reactivas

Sistema de gestión de transacciones bancarias desarrollado con Spring Boot usando programación reactiva. Esta aplicación permite realizar transferencias entre cuentas bancarias de manera asíncrona y no bloqueante.

## 🚀 Tecnologías

- **Spring Boot 3.5.7**: Framework de aplicaciones Java
- **Java 21**: Lenguaje de programación
- **Spring WebFlux**: Framework web reactivo
- **Spring Data R2DBC**: Acceso reactivo a base de datos
- **PostgreSQL**: Base de datos relacional con soporte R2DBC
- **Gradle**: Sistema de construcción y gestión de dependencias
- **Reactor**: Biblioteca para programación reactiva

## 📋 Características

- ✅ Transferencias bancarias entre cuentas
- ✅ Registro histórico de transacciones (tabla de transacciones)
- ✅ Programación reactiva (no bloqueante)
- ✅ Manejo transaccional reactivo
- ✅ Validación de fondos suficientes
- ✅ API REST reactiva con WebFlux

## 🏗️ Estructura del Proyecto

```
transacciones_reactivas/
├── src/
│   ├── main/
│   │   ├── java/com/banco/transacciones_reactivas/
│   │   │   ├── TransaccionesReactivasApplication.java
│   │   │   ├── controller/
│   │   │   │   └── TransacctionController.java
│   │   │   ├── model/
│   │   │   │   └── Cuenta.java
│   │   │   ├── repository/
│   │   │   │   └── CuentaRepository.java
│   │   │   └── services/
│   │   │       └── TransacctionService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/banco/transacciones_reactivas/
│           └── TransaccionesReactivasApplicationTests.java
├── build.gradle
└── settings.gradle
```

## 📦 Modelo de Datos

### Cuenta
- `id`: Identificador único de la cuenta
- `titular`: Nombre del titular de la cuenta
- `saldo`: Saldo disponible en la cuenta
- `fechaCreacion`: Fecha y hora de creación de la cuenta

### Transaccion
- `id`: Identificador único de la transacción
- `cuentaOrigen`: ID de la cuenta origen (mapeado a `cuenta_origen` en BD)
- `cuentaDestino`: ID de la cuenta destino (mapeado a `cuenta_destino` en BD)
- `monto`: Monto transferido
- `fecha`: Fecha y hora de la transacción (se asigna automáticamente)
- `estado`: Estado de la transacción ("COMPLETADA" o "FALLIDA")

## 🔧 Configuración

### Requisitos Previos

- Java 21 o superior
- PostgreSQL 12 o superior
- Gradle 7.x o superior

### Configuración de la Base de Datos

1. Crear una base de datos PostgreSQL:

```sql
CREATE DATABASE banco;
```

2. Crear la tabla de cuentas:

```sql
CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    titular VARCHAR(255) NOT NULL,
    saldo DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

3. Crear la tabla de transacciones (para el registro histórico):

```sql
CREATE TABLE transacciones (
    id BIGSERIAL PRIMARY KEY,
    cuenta_origen BIGINT NOT NULL,
    cuenta_destino BIGINT NOT NULL,
    monto DOUBLE PRECISION NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'COMPLETADA'
);
```

4. Configurar las credenciales en `application.properties`:

```properties
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/banco
spring.r2dbc.username=postgres
spring.r2dbc.password=tu_contraseña
```

## 🚀 Ejecución

### Ejecutar la aplicación

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

O ejecutar directamente:

```bash
java -jar build/libs/transacciones_reactivas-0.0.1-SNAPSHOT.jar
```

La aplicación se iniciará en `http://localhost:8080` (puerto por defecto de Spring Boot).

## 📡 API Endpoints

### Crear Cuenta

**POST** `/api/transacciones/crear-cuenta`

Crea una nueva cuenta bancaria con saldo inicial en 0 y fecha de creación automática.

**Parámetros (query params):**
- `titular` (String): Nombre del titular de la cuenta (mínimo 2 caracteres)

**Ejemplo de petición:**

```bash
curl -X POST "http://localhost:8080/api/transacciones/crear-cuenta?titular=Juan%20Pérez"
```

**Respuestas:**

- `201 Created`: Cuenta creada exitosamente (retorna el objeto Cuenta con id, titular, saldo y fechaCreacion)
- `400 Bad Request`: Error al crear la cuenta (titular vacío, muy corto, etc.)

**Ejemplo de respuesta exitosa:**

```json
{
  "id": 1,
  "titular": "Juan Pérez",
  "saldo": 0.0,
  "fechaCreacion": "2025-11-09T14:30:00"
}
```

### Transferir Fondos

**POST** `/api/transacciones/transferir`

Realiza una transferencia de fondos entre dos cuentas.

**Parámetros (query params):**
- `origen` (Long): ID de la cuenta origen
- `destino` (Long): ID de la cuenta destino
- `monto` (Double): Monto a transferir

**Ejemplo de petición:**

```bash
curl -X POST "http://localhost:8080/api/transacciones/transferir?origen=1&destino=2&monto=100.50"
```

**Respuestas:**

- `200 OK`: Transferencia exitosa
- `400 Bad Request`: Error en la transferencia (cuenta no encontrada, fondos insuficientes, etc.)

## 🧪 Cómo probar con Postman

1) Asegúrate de tener la base de datos y datos de prueba:

```sql
-- Crear base de datos (si no existe)
CREATE DATABASE banco;

-- Conectarte a la BD "banco" y crear las tablas (si no existen)
CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    titular VARCHAR(255) NOT NULL,
    saldo DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transacciones (
    id BIGSERIAL PRIMARY KEY,
    cuenta_origen BIGINT NOT NULL,
    cuenta_destino BIGINT NOT NULL,
    monto DOUBLE PRECISION NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'COMPLETADA'
);

-- Insertar dos cuentas de prueba
INSERT INTO cuentas (titular, saldo) VALUES ('Alice', 1000.00);
INSERT INTO cuentas (titular, saldo) VALUES ('Bob', 500.00);
```

2) Ejecuta la aplicación:

```bash
# Windows
gradlew.bat bootRun
```

3) En Postman crea una petición:

- **Método**: POST
- **URL**: `http://localhost:8080/api/transacciones/transferir`
- **Params** (pestaña Params):
  - `origen` = 1
  - `destino` = 2
  - `monto` = 100.50
- **Body**: vacío (no se requiere body, usa query params)

4) Respuestas esperadas:

- 200 OK con cuerpo: `Transferencia exitosa`
- 400 Bad Request con el mensaje de error correspondiente

5) Verificar en la base de datos:

```sql
-- Ver saldos de las cuentas
SELECT id, titular, saldo FROM cuentas ORDER BY id;

-- Ver el registro de transacciones
SELECT id, cuenta_origen, cuenta_destino, monto, fecha, estado 
FROM transacciones 
ORDER BY fecha DESC;
```

## 🔄 Flujo de Transacciones

1. El servicio busca las cuentas origen y destino
2. Valida que ambas cuentas existan
3. Verifica que la cuenta origen tenga fondos suficientes
4. Actualiza los saldos de ambas cuentas dentro de una transacción reactiva
5. Registra la transacción en la tabla `transacciones` con estado "COMPLETADA" o "FALLIDA"
6. Si ocurre algún error, la transacción se revierte automáticamente y se registra como "FALLIDA"

## 🧪 Pruebas

Ejecutar las pruebas unitarias:

```bash
./gradlew test
```

## 📝 Características Reactivas

Esta aplicación utiliza programación reactiva para:

- **Operaciones no bloqueantes**: Todas las operaciones de I/O son asíncronas
- **Manejo eficiente de recursos**: Mejor aprovechamiento de threads
- **Escalabilidad**: Capaz de manejar múltiples solicitudes concurrentes
- **Transacciones reactivas**: Manejo transaccional usando `ReactiveTransactionManager`

## 🔐 Notas de Seguridad

⚠️ **Importante**: La configuración actual incluye credenciales de base de datos en texto plano (ver `src/main/resources/application.properties`). Para producción, se recomienda:

- Usar variables de entorno
- Configurar un sistema de gestión de secretos (Vault, AWS Secrets Manager, etc.)
- Implementar autenticación y autorización en los endpoints
- Habilitar HTTPS

## 📄 Licencia

Este proyecto es un ejemplo educativo para demostrar el uso de programación reactiva con Spring Boot.

## 👨‍💻 Autor

Desarrollado como proyecto de ejemplo de transacciones reactivas con Spring Boot.

---

**Nota**: Este proyecto utiliza Spring Boot con WebFlux para operaciones reactivas y no bloqueantes, ideal para aplicaciones de alto rendimiento y alta concurrencia.

