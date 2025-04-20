# User Service

A microservice for managing user entities for the SearchMIW application.

## Overview

The User Service is responsible for managing user accounts and provides REST API endpoints to:

- Create new user accounts
- Retrieve user information
- Update user details
- Delete users

**API Documentation:** [Access Swagger UI](http://localhost:8086/swagger-ui.html) when the service is running.

## API Documentation

The API documentation is available via Swagger UI when the service is running:

- **Swagger UI**: [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8086/api-docs](http://localhost:8086/api-docs)

## Technologies

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security**
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
docker build -t user-service .

# Run the container
docker run -p 8086:8086 user-service
```

### Using Docker Compose (with MySQL)

```bash
# Start both the service and a MySQL database
docker-compose up -d
```

## Key API Endpoints

- **GET /api/users**: Get all users
- **GET /api/users/{id}**: Get user by ID
- **GET /api/users/email/{email}**: Find user by email
- **POST /api/users**: Create a new user
- **PUT /api/users/{id}**: Update a user
- **DELETE /api/users/{id}**: Delete a user

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

- **Gateway Service**: For routing API requests
- **Data Aggregator**: For joining user data with other services
