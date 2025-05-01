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

## Authentication Flow

### Registration Process
1. Client sends registration information (name, email, password)
2. Auth service validates the request
3. Auth service calls User service to create a new user
4. On successful creation, a JWT token is generated
5. Response includes user details and the JWT token

### Login Process
1. Client sends email and password
2. Auth service verifies credentials with User service
3. On successful verification, a JWT token is generated
4. Response includes user details and the JWT token

### Token Validation
1. Client sends a request with the Authorization header containing the JWT token
2. Auth service verifies the token's signature and expiration
3. Returns a boolean indicating if the token is valid

## JWT Token Structure

The JWT token contains:
- **Subject**: User's email
- **Claims**: 
  - `userId`: Unique identifier for the user
- **Expiration**: Token validity period (24 hours by default)

## Example Requests and Responses

### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

Response:
```json
{
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Registration successful"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

Response:
```json
{
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Authentication successful"
}
```

### Validate Token
```http
GET /api/auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Response:
```json
true
```

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

## Integration with Other Services

This service integrates with:

- **User Service**: For user authentication and account creation
- **Gateway**: For centralized API routing
- **All protected services**: Any service requiring authentication can validate tokens with this service
