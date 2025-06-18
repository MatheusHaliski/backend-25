# Etapa de build
FROM gradle:8.5-jdk17 as builder

# Instalar JDK 21 manualmente
RUN apt-get update && \
    apt-get install -y wget && \
    wget https://download.java.net/java/GA/jdk21/1/GPL/openjdk-21_linux-x64_bin.tar.gz && \
    tar -xvf openjdk-21_linux-x64_bin.tar.gz && \
    mv jdk-21 /opt/jdk-21

ENV JAVA_HOME=/opt/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

RUN gradle build --no-daemon

# Etapa de execução
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
