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

## Copia el JSON de credenciales
#COPY allygo-bd0a41475b5e.json /app/allygo-bd0a41475b5e.json

# Compila el proyecto sin ejecutar tests
RUN mvn clean package -DskipTests

# Verificar que el JAR se creó y renombrarlo para evitar problemas de versionado
RUN ls -la target/ && \
    mv target/*.jar target/app.jar && \
    ls -la target/

# Expone el puerto de la aplicación
EXPOSE 8080

# Variable de entorno para las credenciales (se sobreescribirá en Cloud Run)
ENV GOOGLE_APPLICATION_CREDENTIALS="/app/gcp-credentials.json"

# Comando para ejecutar la app
CMD ["java", "-jar", "/app/target/app.jar"]