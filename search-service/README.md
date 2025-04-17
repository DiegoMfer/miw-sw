# Search Service

A microservice for searching entities in Wikidata for the SearchMIW application.

## Description

Search Service provides a REST API for searching entities in Wikidata. It includes caching capabilities using Caffeine to improve performance and reduce load on the Wikidata API.

## API Documentation

The API documentation is available using OpenAPI/Swagger.

- **Swagger UI URL**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8084/api-docs](http://localhost:8084/api-docs)

## Features

- Entity search in Wikidata
- Result caching
- Multiple language support

## Technologies

- Java 17
- Spring Boot 3.2.6
- Spring WebFlux for API calls
- Caffeine for caching

## Running the Service

You can run this service using Docker:

```bash
docker build -t search-service .
docker run -p 8084:8084 search-service
```

Or directly with Maven:

```bash
mvn spring-boot:run
```
