# Guía de Despliegue en Render

## Pasos para Desplegar la API en Render

### 1. Preparar el Repositorio Git

Asegúrate de que tu código esté en un repositorio Git (GitHub, GitLab, o Bitbucket):

```bash
git add .
git commit -m "Dockerizar aplicación para Render"
git push origin main
```

### 2. Crear Base de Datos PostgreSQL en Render

1. Ve a tu dashboard de Render: https://dashboard.render.com
2. Click en "New +" → "PostgreSQL"
3. Configura:
   - **Name**: `transacciones-db` (o el nombre que prefieras)
   - **Database**: `banco`
   - **User**: `postgres` (o el que prefieras)
   - **Region**: Elige la región más cercana
   - **PostgreSQL Version**: 16
   - **Plan**: Free (para empezar)
4. Click en "Create Database"
5. **IMPORTANTE**: Guarda las credenciales que Render te proporciona:
   - Internal Database URL
   - External Database URL
   - Username
   - Password
   - Host
   - Port

### 3. Crear el Servicio Web en Render

1. En el dashboard de Render, click en "New +" → "Web Service"
2. Conecta tu repositorio Git
3. Configura el servicio:
   - **Name**: `transacciones-api` (o el nombre que prefieras)
   - **Region**: La misma que elegiste para la base de datos
   - **Branch**: `main` (o la rama que uses)
   - **Root Directory**: Dejar vacío (o `.` si es necesario)
   - **Environment**: `Docker`
   - **Dockerfile Path**: `Dockerfile` (debe estar en la raíz)
   - **Docker Context**: Dejar vacío (o `.`)

### 4. Configurar Variables de Entorno

En la sección "Environment" del servicio web, agrega estas variables:

```
SPRING_R2DBC_URL=r2dbc:postgresql://[HOST]:[PORT]/banco
SPRING_R2DBC_USERNAME=[USERNAME]
SPRING_R2DBC_PASSWORD=[PASSWORD]
SPRING_SQL_INIT_MODE=always
SPRING_SQL_INIT_PLATFORM=postgres
SPRING_PROFILES_ACTIVE=prod
```

**Nota**: Reemplaza `[HOST]`, `[PORT]`, `[USERNAME]`, y `[PASSWORD]` con los valores de tu base de datos PostgreSQL.

**Para obtener la URL correcta:**
- Si usas la **Internal Database URL** de Render, el formato será:
  ```
  r2dbc:postgresql://[host-interno]:5432/banco
  ```
- El host interno lo encuentras en la sección "Connections" de tu base de datos en Render.

### 5. Configurar el Puerto

Render asigna un puerto dinámicamente a través de la variable de entorno `PORT`. 
Necesitas actualizar tu aplicación para usar esta variable.

**Opción A**: Agregar al `application.properties`:
```properties
server.port=${PORT:8080}
```

**Opción B**: Agregar variable de entorno en Render:
```
SERVER_PORT=${PORT}
```

### 6. Desplegar

1. Click en "Create Web Service"
2. Render comenzará a construir tu imagen Docker
3. El proceso puede tardar 5-10 minutos la primera vez
4. Una vez completado, tendrás una URL como: `https://transacciones-api.onrender.com`

### 7. Verificar el Despliegue

- Visita: `https://tu-app.onrender.com/actuator/health`
- Deberías ver un JSON con el estado de salud de la aplicación

## Comandos Útiles

### Probar localmente con Docker

```bash
# Construir la imagen
docker build -t transacciones-api .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5432/banco \
  -e SPRING_R2DBC_USERNAME=postgres \
  -e SPRING_R2DBC_PASSWORD=JhayderSQL \
  transacciones-api
```

### Probar con Docker Compose

```bash
docker-compose up --build
```

## Solución de Problemas

### Error: "Cannot connect to database"
- Verifica que las variables de entorno estén correctamente configuradas
- Asegúrate de usar la **Internal Database URL** si ambos servicios están en Render
- Verifica que el nombre de la base de datos sea correcto

### Error: "Port already in use"
- Render asigna el puerto automáticamente, asegúrate de usar `${PORT}` en la configuración

### Error: "Build failed"
- Verifica que el Dockerfile esté en la raíz del proyecto
- Revisa los logs de build en Render para más detalles

