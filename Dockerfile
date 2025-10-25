FROM ubuntu:latest
LABEL authors="wiecz"

# 1. Etap build – kompilujemy jar
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# kopiujemy pliki projektu
COPY pom.xml .
COPY src ./src

# budujemy jar
RUN mvn clean package -DskipTests

# 2. Etap run – lekki JRE, tylko do uruchomienia
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# kopiujemy zbudowany jar z poprzedniego etapu
COPY --from=builder /app/target/*.jar app.jar

# wystawiamy port
EXPOSE 8080

# komenda startowa
ENTRYPOINT ["java","-jar","app.jar"]
