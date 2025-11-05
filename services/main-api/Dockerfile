
FROM local-trade-builder AS builder


FROM eclipse-temurin:17-jre-jammy
WORKDIR /app


COPY --from=builder /app/services/main-api/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]