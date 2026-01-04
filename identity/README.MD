# Identity API - Car Pooling Platform

This is the **Identity** backend service for the **Car Pooling** platform, built with **Java / Spring Boot**. It handles user identities, authentication, profile management, and ratings.

## 🛠 Main Tech Stack
*   **Java**: 21
*   **Framework**: Spring Boot 3.4.1
*   **Database**: PostgreSQL 16
*   **ORM**: Spring Data JPA + Hibernate
*   **Security**: Spring Security + Java JWT (Auth0)
*   **Documentation**: SpringDoc OpenAPI (Swagger UI)
*   **Containerization**: Docker & Docker Compose

## 📂 Project Structure
The project follows a Clean Architecture approach, organized by functional **modules**:

### 1. **Modules** (`src/main/java/.../modules`)
*   **`user`**: User Registration, Authentication (Login), and User Management.
*   **`profile`**: Management of user profiles (e.g., Passenger, Driver).
*   **`rating`**: Rating system for users.

### 2. **Security** (`src/main/java/.../security`)
*   **`SecurityConfig.java`**: HTTP security filter chains.
*   **`SecurityFilter.java`**: JWT validation filter.
*   **`JwtProvider.java`**: JWT creation and validation logic.

## 🚀 How to Run

### Prerequisites
*   Java 21
*   Docker & Docker Compose

### 1. Start the Database
Use Docker Compose to spin up the PostgreSQL database.

```bash
docker-compose up -d
```

This will start a PostgreSQL container (`postgres_server`) on port `5432` with user `admin` and password `learnJava!2025`.

### 2. Run the Application
You can run the application using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## 🧪 How to Run Tests
The project contains comprehensive unit tests for Mappers, Use Cases, and Controllers.

To run all tests:
```bash
./mvnw test
```

## 📖 API Documentation
Once the application is running, you can access the Swagger UI documentation at:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🐳 Docker Compose Configuration
The `docker-compose.yml` file is configured to run the database service:

```yaml
services:
  db_server:
    image: postgres:16
    container_name: postgres_server
    restart: always
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: learnJava!2025
    ports:
      - "5432:5432"
    volumes:
      - pg_data:/var/lib/postgresql/data
    networks:
      - carpooling_network


networks:
  carpooling_network:
    name: carpooling_network
    driver: bridge

volumes:
  pg_data:
```