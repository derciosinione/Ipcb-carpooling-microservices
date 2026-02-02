# 📡 IPCB Car Pooling - Endpoints & Services

This document lists **all services**, **public URLs**, and **API endpoints** (via Gateway and direct service ports).

---

## 🚀 API Gateway Base URL
All API requests should be directed to the Gateway:
**`http://localhost:8888`**

> [!IMPORTANT]
> All endpoints except for Authentication (`/identity/api/v1/auth/**`) and public ratings/user profile require a valid JWT token in the `Authorization: Bearer <token>` header.

---

# 🧭 Quick Instructions (How to Use)

1) **Deploy / Start (Docker Swarm)**
   - Run: `backend/deploy_swarm.sh`
   - This builds images, ensures DBs, and deploys the full stack.

2) **Get a JWT Token**
   - Call: `POST /identity/api/v1/auth/sign-in`
   - Save `token` from response → use `Authorization: Bearer <token>` in all secured calls.

3) **Open Web UI**
   - Docker: `http://localhost:3003`
   - Local: `http://localhost:3002`

4) **Swagger (API Docs)**
   - Open the service Swagger URLs listed below to test endpoints.

5) **Metrics & Health (Actuator)**
   - Use `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`
   - Trips/Vehicles also expose circuit breaker endpoints.

6) **Logs (Loki + Grafana)**
   - Grafana → Explore → **Loki** datasource
   - Example query: `{stack="carpooling_stack"} |= "ERROR"`

7) **Monitoring**
   - Prometheus: `http://localhost:9090`
   - Grafana: `http://localhost:3000` (admin/admin)

---

# ✅ Services (Local URLs)

| Service | Local URL | Description |
| :--- | :--- | :--- |
| **Gateway** | `http://localhost:8888` | Entry point for API |
| **Identity** | `http://localhost:8080` | Auth, users, profiles, ratings, admin |
| **Vehicles** | `http://localhost:8081` | Vehicles & brands |
| **Trips** | `http://localhost:8083` | Trips, bookings, expenses, metrics |
| **Payments** | `http://localhost:8084` | Payment simulation |
| **GPS** | `http://localhost:8085` | Location search / distance |
| **Notifications** | `http://localhost:8086` | User notifications |
| **Service Discovery** | `http://localhost:8761` | Eureka Dashboard |
| **Frontend (Docker)** | `http://localhost:3003` | Web UI |
| **Frontend (Local)** | `http://localhost:3002` | Web UI |
| **Prometheus** | `http://localhost:9090` | Metrics & scraping |
| **Loki** | `http://localhost:3100` | Centralized logs API |
| **Grafana** | `http://localhost:3000` | Dashboards (admin/admin) |
| **Portainer** | `http://localhost:9000` | Docker management |
| **PgAdmin** | `http://localhost:5050` | DB management |

---

# 🧭 Swagger / OpenAPI (direct service URLs)

| Service | Swagger UI |
| :--- | :--- |
| Identity | `http://localhost:8080/swagger-ui/index.html` |
| Vehicles | `http://localhost:8081/swagger-ui/index.html` |
| Trips | `http://localhost:8083/swagger-ui/index.html` |
| Payments | `http://localhost:8084/swagger` |
| GPS | `http://localhost:8085/swagger-ui/index.html` |
| Notifications | `http://localhost:8086/swagger-ui/index.html` |

---

# 📊 Actuator / Metrics (direct service URLs)

### Trips (8083)
- `/actuator/health`
- `/actuator/metrics`
- `/actuator/prometheus`
- `/actuator/circuitbreakers`
- `/actuator/circuitbreakerevents`

### Vehicles (8081)
- `/actuator/health`
- `/actuator/metrics`
- `/actuator/prometheus`
- `/actuator/circuitbreakers`
- `/actuator/circuitbreakerevents`

### Identity / Payments / GPS / Notifications
- `/actuator/health`
- `/actuator/metrics`
- `/actuator/prometheus`

### Cloud Gateway / Service Discovery
- `/actuator/health`
- `/actuator/metrics`
- `/actuator/prometheus`

---

# 🧾 Logs (Loki)

All backend service logs are collected by **Promtail** and stored in **Loki**.  
You can query logs directly:

- **Loki API**: `http://localhost:3100/loki/api/v1/query`
- Example query: `{stack="carpooling_stack"} |= "ERROR"`

Grafana includes a **Logs panel** in the "CarPooling - Overview" dashboard.

# 🔐 Identity Service (via Gateway `/identity`)

### Authentication (Public)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/identity/api/v1/auth/sign-in` | Authenticate and get JWT |
| `POST` | `/identity/api/v1/auth/register/passenger` | Register passenger |
| `POST` | `/identity/api/v1/auth/register/driver` | Register driver |
| `POST` | `/identity/api/v1/auth/register/both` | Register both roles |

### Users (Secured)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/identity/api/v1/users` | List all users |
| `GET` | `/identity/api/v1/users/{id}` | Get user by ID |
| `GET` | `/identity/api/v1/users/{id}/public` | Public user profile |
| `PUT` | `/identity/api/v1/users/{id}` | Update user (admin/self) |
| `POST` | `/identity/api/v1/users/{id}/profiles/{role}` | Add role |
| `DELETE` | `/identity/api/v1/users/{id}/profiles/{role}` | Remove role |

### Ratings
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/identity/api/v1/ratings` | Create rating |
| `GET` | `/identity/api/v1/ratings/user/{userId}` | Public ratings for user |
| `PUT` | `/identity/api/v1/ratings/{id}` | Update rating |
| `DELETE` | `/identity/api/v1/ratings/{id}` | Delete rating |

### Admin (Secured)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/identity/api/v1/admin/bootstrap` | Create first admin (only once) |
| `POST` | `/identity/api/v1/admin/users` | Create admin |
| `GET` | `/identity/api/v1/admin/users` | List users |
| `PATCH` | `/identity/api/v1/admin/users/{id}/status` | Activate/block user |

---

# 🚗 Vehicles Service (via Gateway `/vehicles`)

### Vehicles
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/vehicles/api/v1/vehicles` | Register vehicle |
| `GET` | `/vehicles/api/v1/vehicles` | List all vehicles |
| `GET` | `/vehicles/api/v1/vehicles/user/{userId}` | Vehicles by owner |
| `DELETE` | `/vehicles/api/v1/vehicles/{id}` | Delete vehicle |

### Brands
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/vehicles/api/v1/brands` | List all brands |

---

# 🗺️ Trips Service (via Gateway `/trips`)

### Trips
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/trips` | Create trip |
| `GET` | `/trips/api/v1/trips` | List all trips |
| `GET` | `/trips/api/v1/trips/available` | List available trips |
| `GET` | `/trips/api/v1/trips/driver/{driverId}` | Trips by driver |
| `GET` | `/trips/api/v1/trips/passenger/{passengerId}` | Trips by passenger |
| `GET` | `/trips/api/v1/trips/search` | Search trips |
| `GET` | `/trips/api/v1/trips/nearby` | Nearby trips |
| `POST` | `/trips/api/v1/trips/{tripId}/status` | Update status |

### Bookings
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/bookings` | Create booking |
| `GET` | `/trips/api/v1/bookings/trip/{tripId}` | Bookings by trip |
| `GET` | `/trips/api/v1/bookings/passenger/{passengerId}` | Bookings by passenger |
| `POST` | `/trips/api/v1/bookings/{id}/accept` | Accept |
| `POST` | `/trips/api/v1/bookings/{id}/reject` | Reject |
| `POST` | `/trips/api/v1/bookings/{id}/cancel` | Cancel |
| `POST` | `/trips/api/v1/bookings/{id}/pay` | Pay |

### Expenses
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/trips/api/v1/expenses` | Add expense |
| `GET` | `/trips/api/v1/expenses/trip/{tripId}` | Expenses by trip |

### Metrics
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/trips/api/v1/metrics?role=DRIVER|PASSENGER` | Metrics |

---

# 💰 Payments Service (via Gateway `/payments`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/payments/api/v1/payments` | Create payment |
| `GET` | `/payments/api/v1/payments/trips/{tripId}` | Payments by trip |

---

# 📍 GPS Service (via Gateway `/gps`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/gps/api/v1/locations/search?q=...&limit=...` | Search locations |
| `POST` | `/gps/api/v1/locations/reverse` | Reverse geocode |
| `POST` | `/gps/api/v1/locations/distance` | Distance between points |
| `POST` | `/gps/api/v1/locations/user/{userId}` | Save recent location |
| `GET` | `/gps/api/v1/locations/user/{userId}` | List recent locations |

---

# 📢 Notifications Service (via Gateway `/notifications`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/notifications/api/v1/notifications` | My notifications |
| `GET` | `/notifications/api/v1/notifications/unread-count` | Unread count |
| `POST` | `/notifications/api/v1/notifications` | Create notification |
| `POST` | `/notifications/api/v1/notifications/read-all` | Mark all read |
| `POST` | `/notifications/api/v1/notifications/{id}/read` | Mark read |
| `DELETE` | `/notifications/api/v1/notifications/{id}` | Delete |

---

# 🌐 Frontend Routes

| Route | Description |
| :--- | :--- |
| `/` | Home |
| `/auth` | Login & Register |
| `/search` | Public search page |
| `/ride/{id}` | Public ride details |
| `/driver/{id}` | Public driver profile |
| `/dashboard` | Dashboard overview |
| `/dashboard/search` | Search rides |
| `/dashboard/rides` | My rides |
| `/dashboard/ride/{id}` | Ride details |
| `/dashboard/vehicles` | Vehicles |
| `/dashboard/publish-ride` | Publish ride |
| `/dashboard/notifications` | Notifications |
| `/dashboard/payments` | Payments |
| `/dashboard/settings` | Profile settings |
| `/dashboard/admin` | Admin panel |

---

# ✅ Example Requests (Quick)

### Login (Get JWT)
```bash
curl -X POST http://localhost:8888/identity/api/v1/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
```

### Search Trips
```bash
curl -X GET "http://localhost:8888/trips/api/v1/trips/search?origin=Lisboa&destination=Porto&seats=1" \
  -H "Authorization: Bearer <TOKEN>"
```

### Create Trip
```bash
curl -X POST http://localhost:8888/trips/api/v1/trips \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"driverId":"<USER_ID>","vehicleId":"<VEHICLE_ID>","origin":"Lisboa","destination":"Porto","departureTime":"2026-02-10T10:00:00","availableSeats":3}'
```
