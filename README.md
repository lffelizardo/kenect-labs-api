# Kenect Labs API
Kenect Labs API is a Spring Boot project that provides secure endpoints for accessing and managing contacts from an external service. It uses JWT authentication to protect endpoints and implements caching to improve response efficiency.

## Project Architecture
The project architecture is modular and layer-based:

1. Presentation Layer (Controller): Exposes the API, manages authentication, and secures the contact endpoints.
2. Service Layer: Contains core business logic, such as ContactService and UserService. Manages authentication, caching, and fallback mechanisms for external data.
3. Integration Layer: Uses Feign to manage calls to external APIs.
4. Cache: Configured with a 3-hour TTL for responses, allowing periodic updates without excessive queries to the external service.

## Key Configurations
The main project configurations are defined in application.yml:

1. JWT Authentication: Configures a secret key for JWT.
2. Redis Cache: Set up to store response data with a 3-hour expiration.
4. H2 Database: Used as an in-memory database for quick testing and development. Since the database is recreated on each execution, a default user with a password is automatically created to facilitate testing.
5. Swagger and OpenAPI: For API documentation and direct endpoint testing.

## Prerequisites
- java 17+
- Maven
- Redis (for the configured 3-hour TTL caching)

## Running the Project
1. Environment Setup: Ensure the settings in application.yml are correct. For JWT, use the default secret key or change it as needed for your environment.

2. Build and Run: To build and run the project, use Maven:
````
./mvnw spring-boot:run
````
3. Accessing the API:
- Login Endpoint: POST /auth/login - To authenticate, send username and password, which returns a JWT.
- Protected Endpoints: Use the generated JWT to access GET /contacts.

## H2 Database for Testing
The project uses an in-memory H2 database. Each time the project is launched, a default test user is created for easy access and functionality verification.
- Accessing the H2 Console: /h2-console
- Default User Credentials: Username and password information for testing are automatically configured at startup.

## Running Tests
The project includes unit and integration tests to verify service functionality and JWT authentication configuration. To run all tests, use:
```
./mvnw test
```
## Example Request
- Login:
  curl -X POST http://localhost:8080/auth/login -d '{
  "username": "user",
  "password": "password"
  }' -H "Content-Type: application/json"

- Accessing Contacts (with generated JWT):
  curl -X GET http://localhost:8080/contacts -H "Authorization: Bearer [your-token]"

## Notes
- Cache: The cache is configured with Redis for a 3-hour TTL and periodic updates.
- Swagger UI: API documentation is available at /swagger-ui.html.