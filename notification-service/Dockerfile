FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar notification-service.jar

ENTRYPOINT ["java","-jar","notification-service.jar"]
