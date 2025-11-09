# Etapa 1: Construcción de la aplicación
FROM gradle:8.11-jdk21-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Descargar dependencias primero (se cachean si no cambian los archivos de configuración)
RUN gradle dependencies --no-daemon || true

# Copiar el código fuente
COPY src ./src

# Construir la aplicación (falla si hay errores)
RUN gradle clean build --no-daemon -x test --stacktrace || \
    (echo "Build failed. Checking for JAR files in build/libs:" && \
     ls -la build/libs/ && \
     exit 1)

# Verificar que se generó el JAR ejecutable
RUN ls -lh build/libs/ && \
    JAR_FILE=$(find build/libs -name "*.jar" ! -name "*-plain.jar" | head -1) && \
    if [ -z "$JAR_FILE" ]; then \
        echo "ERROR: No se encontró JAR ejecutable en build/libs/"; \
        exit 1; \
    fi && \
    echo "JAR encontrado: $JAR_FILE"

# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR construido desde la etapa de build
# Copiar todos los JARs y luego seleccionar el ejecutable (excluyendo *-plain.jar)
COPY --from=build /app/build/libs/*.jar /tmp/
RUN JAR_FILE=$(find /tmp -name "*.jar" ! -name "*-plain.jar" | head -1) && \
    if [ -z "$JAR_FILE" ]; then \
        echo "ERROR: No se encontró JAR ejecutable"; \
        ls -la /tmp/; \
        exit 1; \
    fi && \
    mv "$JAR_FILE" app.jar && \
    rm -f /tmp/*.jar && \
    echo "JAR copiado exitosamente: $(ls -lh app.jar)"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Cambiar propiedad del archivo
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto (pueden ser sobrescritas)
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck para verificar que la aplicación está funcionando
# Instalar wget para el healthcheck
USER root
RUN apk add --no-cache wget
USER spring:spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

