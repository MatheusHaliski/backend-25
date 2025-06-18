# Etapa de build
FROM eclipse-temurin:21-jdk AS builder

# Copia os arquivos do projeto
COPY . /app
WORKDIR /app

# Dá permissão ao wrapper gradle
RUN chmod +x ./gradlew

# Builda o projeto
RUN ./gradlew build --no-daemon

# Etapa de execução
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

