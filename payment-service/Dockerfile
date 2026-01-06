FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar payment-service.jar

ENTRYPOINT ["java","-jar","payment-service.jar"]
