FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .

COPY services/common-dtos/pom.xml services/common-dtos/pom.xml
COPY services/common-dtos/src services/common-dtos/src

COPY services/notification-service/pom.xml services/notification-service/pom.xml

COPY services/main-api/pom.xml services/main-api/pom.xml
COPY services/main-api/src services/main-api/src

RUN mvn clean package -DskipTests -pl services/main-api -am

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/services/main-api/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]