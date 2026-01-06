FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar analytics-service.jar

ENTRYPOINT ["java","-jar","analytics-service.jar"]
