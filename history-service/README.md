# History Service

A microservice for storing and managing user search history for the SearchMIW application.

## Overview

The History Service is responsible for tracking and managing search queries made by users. It provides REST API endpoints to:

- Create new history entries
- Retrieve history entries by user
- Delete specific entries or clear a user's entire history
- Paginate and filter through historical data

**API Documentation:** [Access Swagger UI](http://localhost:8085/swagger-ui.html) when the service is running.

## API Documentation

The API documentation is available via Swagger UI when the service is running:

- **Swagger UI**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8085/api-docs](http://localhost:8085/api-docs)

## Technologies

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **H2 Database** (development)
- **MySQL** (production)
- **Docker** for containerization
- **OpenAPI/Swagger** for API documentation

## Running the Service

### Using Maven

```bash
# Run with H2 in-memory database (development mode)
mvn spring-boot:run

# Run with production profile (requires MySQL)
mvn spring-boot:run -Dspring.profiles.active=prod
```

### Using Docker

```bash
# Build the Docker image
docker build -t history-service .

# Run the container
docker run -p 8085:8085 history-service
```

### Using Docker Compose (with MySQL)

```bash
# Start both the service and a MySQL database
docker-compose up -d
```

## Key API Endpoints

- **GET /api/history**: Get all history entries (paginated)
- **GET /api/history/{id}**: Get entry by ID
- **GET /api/history/user/{userId}**: Get entries for a specific user
- **POST /api/history**: Create a new history entry
- **DELETE /api/history/{id}**: Delete an entry
- **DELETE /api/history/user/{userId}**: Delete all entries for a user

## Development

### Database Configuration

The service uses:
- H2 in-memory database for development
- MySQL for production

Database configuration can be adjusted in:
- `application.properties` (development)
- `application-prod.properties` (production)

### Health Check

The service provides a health endpoint at `/api/health` that returns status information.

## Integration with Other Services

This service is designed to be integrated with:

- **Search Service**: To record search queries
- **Gateway Service**: For authentication and routing
- **Data Aggregator**: For joining history data with other services
