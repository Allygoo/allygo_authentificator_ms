# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:21-jdk AS builder

# Instala Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Directorio de trabajo para el build
WORKDIR /build

# Copia pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Compila el proyecto sin ejecutar tests
RUN mvn clean package -DskipTests

# Verificar y renombrar el JAR
RUN ls -la target/ && \
    mv target/*.jar target/app.jar && \
    ls -la target/app.jar

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre

# Directorio de trabajo para la aplicación
WORKDIR /app

# Copiar SOLO el JAR desde el stage de build
COPY --from=builder /build/target/app.jar /app/app.jar

# Verificar que el JAR existe y tiene permisos correctos
RUN ls -la /app/app.jar && \
    chmod 644 /app/app.jar

# Expone el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la app
CMD ["java", "-jar", "/app/app.jar"]