FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/central-service-jar-with-dependencies.jar /app/central-service.jar
ENTRYPOINT java -jar /app/central-service.jar