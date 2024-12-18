FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/warehouse-service-jar-with-dependencies.jar /app/warehouse-service.jar
CMD ["java", "-jar", "/app/warehouse-service.jar"]
EXPOSE 3344/UDP
EXPOSE 3355/UDP

