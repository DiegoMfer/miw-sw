# SearchMIW Project

This project consists of a microservice architecture with a gateway service, auth service, and a React frontend.

## Getting Started

### Running the Application

Start all services using Docker Compose:

```bash
docker-compose up -d
```

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
