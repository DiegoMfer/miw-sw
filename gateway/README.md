# API Gateway

API Gateway service for the SearchMIW platform, routing requests to appropriate microservices.

## Overview

The Gateway service acts as a single entry point to the SearchMIW microservices architecture, providing:

- **Request Routing** - Directs requests to the appropriate microservice
- **Authentication** - Forwards authentication requests to the auth service
- **API Documentation** - Consolidated view of all service endpoints
- **Health Monitoring** - Central health check endpoint

## Connection Information

### Local Development
- URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

### Docker Environment
- URL: `http://gateway:8080` (internal network)
- URL: `http://localhost:8080` (host machine)

## Available Endpoints

### Services Accessible Through Gateway

| Service | Base Path | Description |
|---------|-----------|-------------|
| User Service | `/api/users` | User account management |
| Search Service | `/api/search` | Wikidata entity search |
| History Service | `/api/history` | User search history |
| Auth Service | `/api/auth` | Authentication and authorization |

### API Documentation Routes

Access each service's API documentation through the gateway:

| Service | Documentation URL |
|---------|------------------|
| Gateway | `/swagger-ui.html` |
| User Service | `/user-api-docs/` |
| Search Service | `/search-api-docs/` |
| History Service | `/history-api-docs/` |

### Health Check Endpoints

| Endpoint | Description |
|----------|-------------|
| `/api/health` | Gateway health status |
| `/api/user-health` | User service health status |
| `/api/search-health` | Search service health status |
| `/api/history-health` | History service health status |

## Example API Calls

### Search for Entities
```
GET http://localhost:8080/api/search?query=Albert%20Einstein&language=en&userId=12345
```

### Get User Search History
```
GET http://localhost:8080/api/history?page=0&size=10
```

### Get All Users
```
GET http://localhost:8080/api/users
```

### User Authentication
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

### User Registration
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

## Configuration

### Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `server.port` | Server port | 8080 |
| `service.user.url` | User service URL | http://localhost:8086 |
| `service.search.url` | Search service URL | http://localhost:8084 |
| `service.history.url` | History service URL | http://localhost:8085 |

### Docker Environment Configuration

For Docker deployment, use the application-prod.properties file which connects to the services using their Docker container names.

## Setup Instructions

### Using Maven
```bash
# Run the service
mvn spring-boot:run
```

### Using Docker
```bash
# Build the Docker image
docker build -t gateway .

# Run the container
docker run -p 8080:8080 gateway
```

## Testing Endpoints

Use these URLs to test the gateway functionality:

```
http://localhost:8080/api/search?query=Albert%20Einstein&language=en&userId=12345
http://localhost:8080/api/history?page=0&size=10
http://localhost:8080/api/users
```

# Testing validation of jwt

## 1. Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123"}'

## 2. Login to get a token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

## 3. Use the token to access a protected endpoint
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjYsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDYyNjQwODksImV4cCI6MTc0NjM1MDQ4OX0.2Wlx1WiS5CmaqC7pg3_waCRJ5XmgBHPyp_gmy3mBXHY"