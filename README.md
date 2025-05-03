# SearchMIW Platform

A microservices-based search platform for retrieving and managing entity information from Wikidata.

## Architecture

The SearchMIW platform consists of the following microservices:

- **Search Service**: Handles search queries to Wikidata
- **User Service**: Manages user accounts and authentication
- **Auth Service**: Handles authentication and JWT token management
- **History Service**: Tracks and stores user search history
- **Aggregator**: GraphQL service that aggregates data from multiple services
- **Gateway**: API Gateway for routing requests to appropriate services
- **Web Client**: React-based frontend application for user interaction

## Services

The platform consists of the following services:

- **Gateway**: API Gateway service running on port 8080
- **User Service**: User management service running on port 8086
- **Search Service**: Search functionality service running on port 8084
- **History Service**: Search history tracking service running on port 8085
- **Auth Service**: Authentication and authorization service running on port 8088
- **Aggregator**: Data aggregation service running on port 8087
- **Web Client**: Frontend application running on port 3000

## Service Documentation

Each service exposes its API documentation through OpenAPI/Swagger:

| Service | Local URL | Docker URL | Port | Description |
|---------|-----------|------------|------|-------------|
| Web Client | [Frontend](http://localhost:3000) | http://localhost:3000 | 3000 | React frontend for user interaction |
| Gateway | [Swagger UI](http://localhost:8080/swagger-ui.html) | http://localhost:8080/swagger-ui.html | 8080 | API Gateway for routing requests |
| Search Service | [Swagger UI](http://localhost:8084/swagger-ui/index.html) | http://localhost:8084/swagger-ui/index.html | 8084 | Service for Wikidata entity search |
| History Service | [Swagger UI](http://localhost:8085/swagger-ui/index.html) | http://localhost:8085/swagger-ui/index.html | 8085 | Service for tracking search history |
| User Service | [Swagger UI](http://localhost:8086/swagger-ui/index.html) | http://localhost:8086/swagger-ui/index.html | 8086 | User account management service |
| Aggregator | [GraphiQL](http://localhost:8087/graphiql) | http://localhost:8087/graphiql | 8087 | GraphQL data aggregation service |
| Auth Service | [Swagger UI](http://localhost:8088/swagger-ui.html) | http://localhost:8088/swagger-ui.html | 8088 | Authentication and authorization service |

**Note:** For accessing services from outside the Docker network, always use `localhost` with the mapped port. The Docker service names (like `gateway:8080`) only work within the Docker network or if configured in your hosts file.

## Running the Platform

### Prerequisites

- Docker and Docker Compose
- Java 17 (for local development)
- Maven (for local development)
- Node.js and npm (for web client development)

### Using Docker Compose

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down
```

### Developing Locally

Each service can be run individually using Maven:

```bash
# Navigate to a service directory
cd <service-directory>

# Build the service
mvn clean install

# Run the service
mvn spring-boot:run
```

For the React web client:

```bash
# Navigate to client directory
cd search-miw-client

# Install dependencies
npm install

# Start development server
npm run dev
```

## Service Health Checks

All services provide health check endpoints at `/api/health`.

## API Gateway Endpoints

The Gateway service exposes the following main endpoints:

- **Search**: `http://localhost:8080/api/search?query=<search_term>&language=<lang>&userId=<id>`
- **History**: `http://localhost:8080/api/history?page=0&size=10`
- **Users**: `http://localhost:8080/api/users`
- **Authentication**: `http://localhost:8080/api/auth/login` and `http://localhost:8080/api/auth/register`

## GraphQL Interface

The Aggregator service provides a GraphQL interface at:
- GraphQL endpoint: `http://localhost:8087/graphql`
- GraphiQL interface: `http://localhost:8087/graphiql`
