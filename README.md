# local-trade: Backend API Platform

A **full-featured Spring Boot backend REST API for a local advertisement trading platform.
It supports user listings, messaging, ratings, and media management — designed for scalability and real-world deployment.

## Tech Stack
- **Java 17**
- **Spring Boot** (REST API, WebSocket chat, validation)
- **PostgreSQL** – relational database  
- **Redis** – caching and token storage  
- **MinIO / AWS S3** – image storage and thumbnail generation  
- **Testcontainers** – integration testing environment  
- **Maven** – build and dependency management  
- **Docker & Docker Compose** – containerized deployment  
- **GitHub Actions** – CI/CD pipeline  
- **Sentry** – error monitoring and observability  


## Core Features
- JWT authentication with access and refresh tokens
- S3/MinIO integration with automatic image thumbnail generation
- "Add to favourites" and user listing tracking
- Real-time chat using WebSockets
- Categories and filtering with pagination
- Rating system with transaction completion logic
- Redis caching for performance optimization
- Over 100 unit and integration tests (Testcontainers + MinIO)
- Swagger UI for API documentation
- One-command startup with Docker Compose
- Environment Configuration


## Environment Configuration

The application requires the following environment variables. Example .env file:

POSTGRES_DB= dbName
DB_USER= dbUserName
DB_PASSWORD= dbPassowrd
JWT_SECRET= JWT Secret Key hs256 encrypted


# S3 / MinIO
- S3_ENDPOINT=http://minio:9000
- S3_BUCKET=/advertisements
- S3_ACCESS_KEY=minioadmin
- S3_SECRET_KEY=minioadmin

Change this env to true to use real S3 bucket 

```bash
s3.useMinio=true
```

# Optional (Redis)
REDIS_HOST=redis
REDIS_PORT=6379


The application-secret.yml (or relevant profile) should reference these environment variables.



## Local Development Setup

Clone the project and start all services using Docker Compose:

```bash
git clone [https://github.com/Czjena/local-trade.git](https://github.com/Czjena/local-trade.git)
cd local-trade
docker-compose up --build
```

Swagger UI will be available at:
http://localhost:8080/swagger-ui.html


## Testing
The project utilizes Testcontainers to run integration tests in isolated, containerized environments.
To execute all tests:

```bash
mvn test
```

This command automatically provisions PostgreSQL, Redis, and MinIO containers for the integration test suite.

## API Documentation

## API documentation is available via the following endpoints:
Swagger UI: /swagger-ui.html
OpenAPI v3 Specification: /v3/api-docs

## CI/CD Pipeline
The GitHub Actions workflow executes the following:
Build and test using Maven
Code quality checks via Qodana
Static analysis and test coverage reports

## Architectural Overview
Layered architecture (controller → service → repository)
External integrations (S3, Redis, DB) are mocked via Testcontainers during the test phase
Ready for deployment with minimal configuration changes

Docker Compose orchestrates the database, cache, and object storage

## License
This project is licensed under the MIT License.
See the LICENSE file for details.

## Project Roadmap
AI-based image moderation
WebSocket or Kafka-based notification system
CI/CD deployment to a staging environment
Frontend integration (React/Next.js)

## Author
Adrian Wieczorek (Czjena)
GitHub: @Czjena
