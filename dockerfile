# Etapa de construcción
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
# Copiamos solo los archivos necesarios primero para aprovechar la cache
COPY pom.xml .
COPY src/main/resources/proyecto-sig-247c3-firebase-adminsdk-fbsvc-e218dde821.json src/main/resources/
RUN mvn dependency:go-offline

# Copiamos el resto del proyecto
COPY . .

# Compilamos el proyecto
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jdk

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el jar compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Copiamos el archivo de credenciales de Firebase
COPY --from=builder /app/src/main/resources/proyecto-sig-247c3-firebase-adminsdk-fbsvc-e218dde821.json /app/resources/proyecto-sig-247c3-firebase-adminsdk-fbsvc-e218dde821.json

# Exponemos el puerto de la aplicación (cambia si usas otro)
EXPOSE 8080

# Variable de entorno opcional (puedes configurar más)
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
