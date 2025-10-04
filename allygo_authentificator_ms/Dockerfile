# Usa JDK 21
FROM eclipse-temurin:21-jdk

# Instala Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Copia el JSON de credenciales
COPY allygo-8838fb41d42f.json /app/allygo-8838fb41d42f.json

# Compila el proyecto sin ejecutar tests
RUN mvn clean package -DskipTests

# Expone el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la app
CMD ["java", "-jar", "target/allygo-authentificator-ms-0.0.1-SNAPSHOT.jar"]
