FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar loan-application-service.jar

ENTRYPOINT ["java","-jar","loan-application-service.jar"]
