# Etapa 1: Construção da aplicação
FROM gradle:8.5.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build --no-daemon

# Etapa 2: Criação da imagem final
FROM eclipse-temurin:17-jre
EXPOSE 8080
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /home/gradle/project/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
