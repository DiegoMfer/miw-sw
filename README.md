# SearchMIW Microservices Architecture

This project implements a microservices architecture for the SearchMIW application.

## Services

- **Gateway Service**: Entry point for all client requests, handles routing and authentication
- **Auth Service**: Handles user authentication and JWT token generation
- **User Service**: Manages user profiles, credentials, and user metadata
- **Data Aggregator**: GraphQL API that aggregates data from multiple services
- **History Service**: Stores and manages user search history

## Integration Between Auth Service and User Service

The Auth Service and User Service work together to provide complete user management functionality:

1. **Auth Service** focuses on:
   - JWT token generation and validation
   - Authentication orchestration

2. **User Service** focuses on:
   - User data storage and management
   - User profile operations
   - Password encryption and validation

### Authentication Flow

1. User submits login credentials to `/auth/login`
2. Gateway forwards request to Auth Service
3. Auth Service forwards credentials to User Service
4. User Service verifies credentials and returns user profile
5. Auth Service generates JWT token based on user profile
6. Token is returned to client

### Registration Flow

1. User submits registration info to `/auth/register`
2. Gateway forwards request to Auth Service
3. Auth Service checks with User Service if email already exists
4. Auth Service sends user data to User Service for creation
5. User Service creates user record and returns profile
6. Auth Service generates JWT token based on new user profile
7. Token is returned to client

## Running the Services

Use Docker Compose to start all services:

```
docker-compose up -d
```

The application will be available at http://localhost:8080

### Accessing the Application

#### Frontend Application

The React frontend is available at:

**http://localhost:5173**

This runs the frontend service directly on port 5173.

#### Through the API Gateway

The gateway service redirects the root path to the frontend:

**http://localhost:8080**

The API Gateway also provides:
- Authentication endpoints at `/auth/*`
- API documentation at `/api`

## Service Architecture

- **Gateway Service**: Port 8080 - Handles API routing and JWT validation
- **Auth Service**: Port 8081 - Handles authentication and user management
- **Frontend**: Port 5173 - React application

## Development Notes

- Environment variables are configured in the Docker Compose file
- Frontend API calls are directed to the Gateway service
- Authentication is handled via JWT tokens

## Troubleshooting

If you get a 404 error on the gateway (http://localhost:8080):

1. Make sure all services are running: `docker-compose ps`
2. Check logs for any errors: `docker-compose logs gateway`
3. Try accessing the frontend directly at http://localhost:5173
4. Verify the API is accessible at http://localhost:8080/api
