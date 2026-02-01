# 📡 IPCB Car Pooling - API Endpoints

This document lists the available endpoints for the microservices, accessible through the **API Gateway**.

## 🚀 API Gateway Base URL
All requests should be directed to the Gateway:
**`http://localhost:8888`**

> [!IMPORTANT]
> All endpoints except for Authentication (`/identity/api/v1/auth/**`) require a valid JWT token in the `Authorization: Bearer <token>` header.

---

## 🔐 Identity Service
**Gateway Prefix**: `/identity`

### Authentication (Public)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/identity/api/v1/auth/sign-in` | Authenticate and get JWT token, ID, and roles |
| `POST` | `/identity/api/v1/auth/register/passenger` | Register a new passenger |
| `POST` | `/identity/api/v1/auth/register/driver` | Register a new driver |
| `POST` | `/identity/api/v1/auth/register/both` | Register a user with both roles |

**Sign-In Response Example:**
```json
{
  "id": "uuid-here",
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzI1Ni...",
  "roles": ["Condutor"]
}
```

### Users (Secured)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/identity/api/v1/users` | List all users |
| `GET` | `/identity/api/v1/users/{id}` | Get user by ID |
| `PUT` | `/identity/api/v1/users/{id}` | Update user information |
| `POST` | `/identity/api/v1/users/{id}/profiles/{role}` | Add a profile (role) to user |
| `DELETE` | `/identity/api/v1/users/{id}/profiles/{role}` | Remove a profile from user |

---

## 🚗 Vehicles Service (Secured)
**Gateway Prefix**: `/vehicles`

### Vehicles
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/vehicles/api/v1/vehicles` | Register a new vehicle |
| `GET` | `/vehicles/api/v1/vehicles` | List all vehicles |
| `GET` | `/vehicles/api/v1/vehicles/user/{userId}` | List vehicles by owner ID |

### Brands & Models
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/vehicles/api/v1/brands` | List all car brands |
| `GET` | `/vehicles/api/v1/models` | List all car models |

---

## 🗺️ Trips Service (Secured)
**Gateway Prefix**: `/trips`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/trips` | Create a new trip |
| `GET` | `/trips/api/v1/trips` | List all trips |
| `GET` | `/trips/api/v1/trips/available` | List available trips |
| `GET` | `/trips/api/v1/trips/driver/{driverId}` | List trips by driver |
| `GET` | `/trips/api/v1/trips/passenger/{passengerId}` | List trips by passenger |
| `GET` | `/trips/api/v1/trips/search` | Search trips by origin/destination |

---

## 💰 Payments Service (Secured)
**Gateway Prefix**: `/payments`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/payments/api/v1/payments` | Create a new payment |
| `GET` | `/payments/api/v1/payments/trips/{tripId}` | Get payments by trip ID |
| `GET` | `/payments/api/v1/payments/ping` | Health check ping |

---

## 🛠️ Infrastructure
| Service | Local URL | Description |
| :--- | :--- | :--- |
| **Eureka Dashboard** | `http://localhost:8761` | Service Registry Monitoring |
| **Portainer** | `http://localhost:9000` | Docker Container Management |
| **PgAdmin** | `http://localhost:5050` | Database Management |
| **Frontend (Docker)** | `http://localhost:3003` | User Web Interface |
| **Frontend (Local)** | `http://localhost:3002` | User Web Interface |
