# Etapa 1: Construcción de la aplicación
FROM gradle:8.11-jdk21-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Descargar dependencias (se cachean si no cambian los archivos de configuración)
RUN gradle dependencies --no-daemon || true

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN gradle build --no-daemon -x test

# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar el JAR construido desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar propiedad del archivo
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto (pueden ser sobrescritas)
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck para verificar que la aplicación está funcionando
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

