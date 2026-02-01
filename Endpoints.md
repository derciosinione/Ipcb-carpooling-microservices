# 📡 IPCB Car Pooling - API Endpoints

This document lists the available endpoints for the microservices, accessible through the **API Gateway**, as well as the web frontend routes.

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

### Users (Secured)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/identity/api/v1/users` | List all users |
| `GET` | `/identity/api/v1/users/{id}` | Get user by ID |
| `PUT` | `/identity/api/v1/users/{id}` | Update user information |
| `POST` | `/identity/api/v1/users/{id}/profiles/{role}` | Add a profile (role) to user |
| `DELETE` | `/identity/api/v1/users/{id}/profiles/{role}` | Remove a profile from user |

### Ratings (Secured)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/identity/api/v1/ratings` | Create a new rating |
| `GET` | `/identity/api/v1/ratings/user/{userId}` | Get ratings for a specific user |
| `PUT` | `/identity/api/v1/ratings/{id}` | Update an existing rating |
| `DELETE` | `/identity/api/v1/ratings/{id}` | Delete a rating |

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

### Trip Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/trips` | Create a new trip |
| `GET` | `/trips/api/v1/trips` | List all trips |
| `GET` | `/trips/api/v1/trips/available` | List available trips |
| `GET` | `/trips/api/v1/trips/driver/{driverId}` | List trips by driver |
| `GET` | `/trips/api/v1/trips/passenger/{passengerId}` | List trips by passenger |
| `GET` | `/trips/api/v1/trips/search` | Search trips by origin/destination |

### Bookings
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/bookings` | Book seats on a trip |
| `PATCH` | `/trips/api/v1/bookings/{id}/accept` | Accept a booking request |
| `PATCH` | `/trips/api/v1/bookings/{id}/reject` | Reject a booking request |

### Expenses
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/expenses` | Register an expense for a trip |

---

## 💰 Payments Service (Secured)
**Gateway Prefix**: `/payments`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/payments/api/v1/payments` | Create a new payment |
| `GET` | `/payments/api/v1/payments/trips/{tripId}` | Get payments by trip ID |

---

## 🌐 Web Frontend Routes
The frontend provides a user-friendly interface for interacting with the microservices.

| Route | Description |
| :--- | :--- |
| `/` | Home Page - Introduction and search entry |
| `/auth` | Authentication Page - Login and Registration |
| `/search` | Search Results Page |
| `/ride/{id}` | Public Ride Details |
| `/driver/{id}` | Driver Profile and Ratings |
| `/dashboard` | User Dashboard Overview |
| `/dashboard/rides` | User's Published/Joined Rides |
| `/dashboard/vehicles` | Vehicle Management |
| `/dashboard/publish-ride` | Form to publish a new ride |
| `/dashboard/settings` | Account/Profile Settings |

---

## 🛠️ Infrastructure
| Service | Local URL | Description |
| :--- | :--- | :--- |
| **Eureka Dashboard** | `http://localhost:8761` | Service Registry Monitoring |
| **Portainer** | `http://localhost:9000` | Docker Container Management |
| **PgAdmin** | `http://localhost:5050` | Database Management |
| **Frontend (Docker)** | `http://localhost:3003` | User Web Interface |
| **Frontend (Local)** | `http://localhost:3002` | User Web Interface |
