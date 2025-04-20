# Data Aggregator Service

A GraphQL-based aggregation service that combines data from the User, Search, and History services into a single unified API.

## Overview

The Data Aggregator Service implements the Aggregator pattern to provide clients with a simplified, consolidated API for accessing functionality across multiple microservices. Instead of making multiple API calls to different services, clients can make a single GraphQL request to get all the data they need.

## Features

- **Unified GraphQL API**: Access data from multiple services with a single request
- **Query relationships**: Get related data across services (e.g., a user's search history)
- **Type safety**: GraphQL schema provides clear type definitions
- **Efficient queries**: Clients can request only the fields they need
- **Error handling**: Comprehensive error handling with detailed error messages

## GraphQL Schema

The service exposes the following main types:
- **User**: User account information
- **SearchResult**: Results from search queries
- **HistoryEntry**: Historical record of user searches

## Accessing the GraphQL API

When the service is running, you can access:

- **GraphQL Endpoint**: [http://localhost:8087/graphql](http://localhost:8087/graphql)
- **GraphiQL UI**: [http://localhost:8087/graphiql](http://localhost:8087/graphiql) - Interactive GraphQL IDE

## Example Queries

### Get a user with their search history

```graphql
query {
  user(id: 1) {
    id
    username
    email
    searchHistory {
      id
      query
      timestamp
    }
  }
}
```

### Perform a search and save it to history

```graphql
mutation {
  saveSearch(userId: 1, query: "Albert Einstein") {
    success
    message
    historyEntry {
      id
      query
      timestamp
      searchResult {
        totalResults
        results {
          title
          description
          url
        }
      }
    }
  }
}
```

### Create a new user with error handling

```graphql
mutation {
  createUser(input: {
    username: "newuser",
    email: "newuser@example.com",
    password: "password123",
    name: "New User"
  }) {
    success
    message
    user {
      id
      username
      email
    }
  }
}
```

### Correct mutation query structure

Note that all mutation responses now follow this pattern:

```graphql
mutation {
  createUser(input: {
    username: "Diego",
    email: "diego@gmail.com",
    password: "1234gsdfg12",
    name: "Diegoas"
  }) {
    success
    message
    user {
      id
      username
      email
      name
    }
  }
}
```

## Error Handling

All mutations return response objects that include:
- `success` - Boolean indicating if the operation was successful
- `message` - Descriptive message (especially useful for errors)
- The requested entity (if applicable and successful)

## Technologies

- **Java 17**
- **Spring Boot 3**
- **Spring for GraphQL**
- **WebClient** for service-to-service communication
- **Docker** for containerization

## Running the Service

### Using Maven

```bash
# Run the service locally
mvn spring-boot:run
```

### Using Docker

```bash
# Build the Docker image
docker build -t data-aggregator-service .

# Run the container
docker run -p 8087:8087 data-aggregator-service
```

### Using Docker Compose

```bash
# From the root project directory
docker-compose up -d
```

## Service Dependencies

This service aggregates data from:

- **User Service**: Manages user accounts (port 8086)
- **Search Service**: Provides search functionality (port 8084)
- **History Service**: Stores search history (port 8085)

Make sure these services are running and accessible for the aggregator to work properly.
