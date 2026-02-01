# 📡 IPCB Car Pooling - API Endpoints

This document lists the available endpoints for the microservices, accessible through the **API Gateway**.

## 🚀 API Gateway Base URL
All requests should be directed to the Gateway:
**`http://localhost:8888`**

---

## 🔐 Identity Service
**Gateway Prefix**: `/identity`

### Authentication
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/identity/api/v1/auth/sign-in` | Authenticate user and get JWT token |
| `POST` | `/identity/api/v1/auth/register/passenger` | Register a new passenger |
| `POST` | `/identity/api/v1/auth/register/driver` | Register a new driver |
| `POST` | `/identity/api/v1/auth/register/both` | Register a user with both roles |

### Users
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/identity/api/v1/users` | List all users |
| `GET` | `/identity/api/v1/users/{id}` | Get user by ID |
| `PUT` | `/identity/api/v1/users/{id}` | Update user information |
| `POST` | `/identity/api/v1/users/{id}/profiles/{role}` | Add a profile (role) to user |
| `DELETE` | `/identity/api/v1/users/{id}/profiles/{role}` | Remove a profile from user |

### Profiles
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/identity/api/v1/profiles` | List all profiles |
| `GET` | `/identity/api/v1/profiles/{id}` | Get profile by ID |
| `POST` | `/identity/api/v1/profiles` | Create a new profile |

---

## 🚗 Vehicles Service
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

### Health Checks
| Service | Endpoint |
| :--- | :--- |
| Identity | `http://localhost:8888/identity/api/v1/health` |
| Vehicles | `http://localhost:8888/vehicles/api/v1/health` |

---

## 🗺️ Other Services
The following services are registered in Service Discovery but may not have explicit Gateway routes yet:

- **Trips Service**: Registered in Eureka.
- **Payments Service**: Registered in Eureka.
- **Frontend**: Registered as `CARPOOLING-FRONTEND`.

---

## 🛠️ Infrastructure
| Service | Local URL | Description |
| :--- | :--- | :--- |
| **Eureka Dashboard** | `http://localhost:8761` | Service Registry Monitoring |
| **Portainer** | `http://localhost:9000` | Docker Container Management |
| **PgAdmin** | `http://localhost:5050` | Database Management |
| **Frontend (Docker)** | `http://localhost:3003` | User Web Interface |
| **Frontend (Local)** | `http://localhost:3002` | User Web Interface |
