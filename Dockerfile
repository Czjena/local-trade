FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app


COPY pom.xml .
COPY services/common-dtos/pom.xml ./services/common-dtos/
COPY services/main-api/pom.xml ./services/main-api/
COPY services/notification-service/pom.xml ./services/notification-service/


RUN mvn -B dependency:resolve dependency:resolve-plugins


COPY services/common-dtos/src ./services/common-dtos/src/
COPY services/main-api/src ./services/main-api/src/
COPY services/notification-service/src ./services/notification-service/src/


RUN mvn -B clean package -DskipTests