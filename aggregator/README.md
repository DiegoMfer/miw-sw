# Data Aggregator Service

A GraphQL-based data aggregator service for the SearchMIW application.

## Overview

The Data Aggregator Service provides a unified GraphQL API that consolidates data from various microservices within the SearchMIW ecosystem, including the User Service.

## Features

- GraphQL API for querying user data
- Integration with the User Service REST API

## Technologies

- **Java 17**
- **Spring Boot 3**
- **GraphQL**
- **WebClient** for API communication
- **Spring WebFlux** for reactive programming

## Running the Service

### Using Maven

```bash
# Run the service
mvn spring-boot:run
```

### Using Docker

```bash
# Build the Docker image
docker build -t aggregator .

# Run the container
docker run -p 8087:8087 aggregator
```

## GraphQL Endpoint

- **GraphQL API**: `http://localhost:8087/graphql`
- **GraphiQL Interface**: `http://localhost:8087/graphiql` (for testing queries in browser)

## Sample Queries

### Get all users

```graphql
query {
  users {
    id
    name
    email
    createdAt
    updatedAt
  }
}
```

### Get user by ID

```graphql
query {
  userById(id: "1") {
    id
    name
    email
    createdAt
    updatedAt
  }
}
```

## Configuration

The service is configured to communicate with:
- User Service at `http://localhost:8086` (configurable in application.properties)

## Integration with Other Services

This service currently aggregates data from:
- **User Service**: For user information
