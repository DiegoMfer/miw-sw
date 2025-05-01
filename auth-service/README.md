# Auth Service

Authentication and authorization service for the SearchMIW platform.

## Connection Information

### Local Development
- URL: `http://localhost:8088`
- Swagger UI: `http://localhost:8088/webjars/swagger-ui/index.html`
- API Docs: `http://localhost:8088/api-docs`

### Docker Environment
- URL: `http://auth-service:8088` (internal network)
- URL: `http://localhost:8088` (host machine)

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/validate` - JWT token validation

### Health Check
- `GET /api/health` - Service health status check

## Setup Instructions

1. Build the service:
   ```bash
   mvn clean install
   ```

2. Run locally:
   ```bash
   mvn spring-boot:run
   ```

3. Run with Docker:
   ```bash
   docker build -t auth-service .
   docker run -p 8088:8088 auth-service
   ```

## Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `server.port` | Server port | 8088 |
| `jwt.expiration` | JWT expiration time (ms) | 86400000 (24 hours) |
| `services.user.url` | User service URL | http://user-service:8086 |

## Dependencies

- Spring Boot 3.4.5
- Spring WebFlux
- JJWT (JSON Web Token)
- SpringDoc OpenAPI

## API Documentation

The service uses SpringDoc OpenAPI for documentation. When the service is running, you can access:
- Swagger UI: `http://localhost:8088/webjars/swagger-ui/index.html`
- OpenAPI specification: `http://localhost:8088/api-docs`
