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
   - **PostgreSQL Version**: 17 (o la versión que prefieras, 17 es la predeterminada y recomendada)
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
   - **Root Directory**: `transacciones_reactivas/transacciones_reactivas` ⚠️ **IMPORTANTE**: Si tu proyecto está en un subdirectorio, pon la ruta aquí
   - **Environment**: `Docker`
   - **Dockerfile Path**: `Dockerfile` (debe estar en la raíz del proyecto, no del repo)
   - **Docker Context**: Dejar vacío (o `.`)

### 4. Configurar Variables de Entorno

En la sección "Environment" del servicio web, agrega estas variables:

```
SPRING_R2DBC_URL=postgresql://postgres_admin:r9DTP1csPgpaBssWXYRZ30liv5alGWgT@dpg-d48cljer433s73a0i72g-a/banco_e6a2
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

### Error: "Build failed" o "failed to read dockerfile: open Dockerfile: no such file or directory"

Este error generalmente ocurre por una de estas razones:

1. **El Dockerfile no está en el repositorio Git:**
   ```bash
   # Verifica que el Dockerfile esté en Git
   git status
   
   # Si no aparece, agrégalo y haz commit:
   git add Dockerfile
   git add docker-compose.yml
   git commit -m "Agregar Dockerfile y docker-compose.yml"
   git push origin main
   ```

2. **Configuración incorrecta en Render:**
   - **Root Directory**: Debe estar **vacío** (no poner `.` ni `/`)
   - **Dockerfile Path**: Debe ser exactamente `Dockerfile` (sin ruta, sin extensión)
   - **Docker Context**: Debe estar **vacío** (no poner `.`)

3. **El Dockerfile está en un subdirectorio:**
   - Si tu Dockerfile está en otro lugar, ajusta el **Dockerfile Path** en Render
   - Por ejemplo: si está en `docker/Dockerfile`, pon `docker/Dockerfile` en el campo

4. **Verifica que el Dockerfile esté en la raíz del proyecto:**
   - El Dockerfile debe estar al mismo nivel que `build.gradle` y `src/`
   - Revisa los logs de build en Render para más detalles

### Error: "failed to solve: failed to compute cache key: "/src": not found"

Este error indica que Docker no puede encontrar el directorio `src` durante el build. Soluciones:

1. **Verificar que `src/` esté en Git:**
   ```bash
   git status src/
   # Si no aparece o dice "untracked", agrégalo:
   git add src/
   git commit -m "Agregar directorio src al repositorio"
   git push origin main
   ```

2. **Verificar la configuración en Render (SOLUCIÓN PARA TU CASO):**
   
   Si tu proyecto está en un subdirectorio como `transacciones_reactivas/transacciones_reactivas/`:
   
   - **Root Directory**: Debe ser `transacciones_reactivas/transacciones_reactivas` (la ruta desde la raíz del repo hasta tu proyecto)
   - **Docker Context**: Debe estar **vacío** (no poner `.`)
   - **Dockerfile Path**: Debe ser `Dockerfile` (relativo al Root Directory)
   
   **Ejemplo de estructura:**
   ```
   repo-raiz/
     └── transacciones_reactivas/
         └── transacciones_reactivas/  ← Root Directory aquí
             ├── src/
             ├── build.gradle
             └── Dockerfile
   ```

3. **Verificar que `.dockerignore` no ignore `src/`:**
   - El `.dockerignore` solo debe ignorar `src/test/`, NO `src/` completo
   - Asegúrate de que no haya una línea que diga solo `src/` o `src`

### Error: "Exited with status 1 while building your code"

Este error indica que el build de Docker está fallando. Posibles causas:

1. **Error en el build de Gradle:**
   - Revisa los logs completos en Render para ver el error específico
   - Verifica que todas las dependencias estén correctas en `build.gradle`
   - Asegúrate de que el código compile localmente primero:
     ```bash
     ./gradlew clean build -x test
     ```

2. **Problemas con el JAR:**
   - Verifica que Spring Boot esté generando el JAR correctamente
   - El JAR debe estar en `build/libs/` después del build
   - Verifica que no haya errores de compilación en el código Java

3. **Problemas de memoria durante el build:**
   - Render puede tener límites de memoria
   - Intenta simplificar el Dockerfile o reducir las dependencias

4. **Para depurar:**
   - Revisa los logs completos en Render (haz clic en "View logs")
   - Busca líneas que digan "ERROR" o "FAILED"
   - El error real suele aparecer antes del "Exited with status 1"

5. **Solución temporal - Build local:**
   ```bash
   # Construir localmente para ver el error
   docker build -t test-build .
   
   # Si funciona localmente, el problema puede ser específico de Render
   ```

