FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar loan-processing-service.jar

ENTRYPOINT ["java","-jar","loan-processing-service.jar"]
