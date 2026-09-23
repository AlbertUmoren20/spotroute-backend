# SpotRoute Backend — Spring Boot

**What Is SpotRoute**

SpotRoute is a carpooling and shared-mobility technology platform developed to address the daily transportation needs of Lagos commuters. The platform connects private vehicle owners with commuters travelling along the same route, converting unused vehicle capacity into an affordable, comfortable, and reliable mode of transport.

SpotRoute occupies the space between costly private ride-hailing services and overcrowded public transit: it offers the comfort and safety of a private vehicle, shared among passengers travelling the same route, at a fixed and transparent price agreed in advance.

## Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Build | Maven |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Auth | Spring Security + JWT (jjwt 0.12.5) |
| Payments | Flutterwave v3 REST API |
| HTTP Client | Spring WebFlux WebClient |

---

## Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8+

---

## Setup

```bash
cd spotroute-backend

# 1. Copy env file
cp .env.example .env
# Edit .env and fill in your DB credentials, JWT secret, Flutterwave keys

# 2. Create the MySQL database
mysql -u root -p -e "CREATE DATABASE spotroute CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. Run — schema is auto-created by Hibernate, routes are seeded on first boot
./mvnw spring-boot:run
```

The server starts on **http://localhost:8080/api**.

---

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `...` |
| `DB_NAME` | Database name | `spotroute` |
| `DB_USER` | MySQL user | `root` |
| `DB_PASSWORD` | MySQL password | _(empty)_ |
| `JWT_SECRET` | HS256 signing secret (32+ chars) | see `.env.example` |
| `JWT_EXPIRATION_MS` | Token lifetime in ms | `86400000` (24h) |
| `FLUTTERWAVE_SECRET_KEY` | Flutterwave secret key | — |
| `FLUTTERWAVE_PUBLIC_KEY` | Flutterwave public key | — |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins | `http://localhost:3000,http://localhost:5000` |

---

## API Reference

All responses follow the envelope:
```json
{ "success": true, "message": "...", "data": { ... } }
```

### Auth — `/api/auth`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register USER or DRIVER |
| POST | `/auth/login` | Public | Login, returns JWT |
| GET | `/auth/me` | Bearer | Current user profile |

**Register body:**
```json
{
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "password": "Passw0rd!",
  "phone": "+2348012345678",
  "role": "USER"
}
```
For `role: "DRIVER"` also include `carModel`, `carPlate`, `carColor`.

---

### Rides — `/api/rides`

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/rides/available` | Public | All upcoming scheduled rides |
| POST | `/rides` | DRIVER | Publish a new ride |
| GET | `/rides/my` | DRIVER | Driver's own rides |

**Create ride body:**
```json
{
  "routeId": "<uuid>",
  "departureTime": "2025-06-01T08:00:00",
  "totalSeats": 4
}
```

---

### Routes — `/api/routes`

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/routes` | Public | All available routes |

Default seeded routes (inserted on first boot):
- Gbagada → Victoria Island — ₦1,000
- Yaba → Lekki — ₦1,500
- Surulere → Ikoyi — ₦800
- Ikeja → Marina — ₦1,200

---

### Bookings — `/api/bookings`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/bookings` | Bearer | Create a booking |
| GET | `/bookings/user/me` | Bearer | Current user's bookings |
| GET | `/bookings/{id}` | Bearer | Single booking |

**Create booking body:**
```json
{
  "rideId": "<uuid>",
  "seatCount": 2,
  "pickupPoint": "Gbagada Phase 1"
}
```

---

### Payments — `/api/payments`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/payments/initialize` | Bearer | Get Flutterwave payment link |
| POST | `/payments/verify` | Bearer | Verify transaction after redirect |
| POST | `/payments/webhook` | Public | Flutterwave webhook receiver |

**Initialize body:**
```json
{
  "bookingId": "<uuid>",
  "redirectUrl": "http://localhost:5000/payment/callback"
}
```

**Verify body:**
```json
{
  "transactionId": "1234567",
  "paymentReference": "SPR-XXXXXXXXXXXX"
}
```

On successful verification the booking status → `CONFIRMED`, driver wallet is credited automatically.

---

### Wallet — `/api/wallet` _(DRIVER only)_

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/wallet` | DRIVER | Balance + transaction history |
| POST | `/wallet/payout` | DRIVER | Request a payout |

**Payout body:**
```json
{
  "amount": 5000.00,
  "accountNumber": "0123456789",
  "bankCode": "044",
  "accountName": "Ada Lovelace"
}
```

---

### Health

| Method | Path | Auth |
|---|---|---|
| GET | `/health` | Public |

---

## Quick Test (curl)

```bash
# Register a user
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada","email":"ada@example.com","password":"Passw0rd!","phone":"+2348012345678","role":"USER"}' | jq .

# Login and capture token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ada@example.com","password":"Passw0rd!"}' | jq -r '.data.token')

# Get available rides
curl -s http://localhost:8080/api/rides/available | jq .

# Register a driver
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Chidi","email":"chidi@example.com","password":"Passw0rd!","phone":"+2348099999999","role":"DRIVER","carModel":"Toyota Camry","carPlate":"LND-001AB","carColor":"Black"}' | jq .

DRIVER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"chidi@example.com","password":"Passw0rd!"}' | jq -r '.data.token')

# Get routes
ROUTE_ID=$(curl -s http://localhost:8080/api/routes | jq -r '.data[0].id')

# Create a ride
RIDE_ID=$(curl -s -X POST http://localhost:8080/api/rides \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"routeId\":\"$ROUTE_ID\",\"departureTime\":\"2026-12-01T08:00:00\",\"totalSeats\":4}" | jq -r '.data.id')

# Book the ride
curl -s -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"rideId\":\"$RIDE_ID\",\"seatCount\":1,\"pickupPoint\":\"Gbagada Phase 1\"}" | jq .
```

---

## Project Structure

```
src/main/java/com/spotroute/
├── SpotRouteApplication.java
├── config/
│   ├── DataSeeder.java          # Seeds default routes on first boot
│   └── SecurityConfig.java      # Spring Security + CORS
├── controller/
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── HealthController.java
│   ├── PaymentController.java
│   ├── RideController.java
│   ├── RouteController.java
│   └── WalletController.java
├── dto/
│   ├── request/                 # Validated inbound payloads
│   └── response/                # Outbound API shapes
├── entity/                      # JPA entities (MySQL tables)
├── exception/                   # Custom exceptions + GlobalExceptionHandler
├── middleware/
│   ├── CustomUserDetailsService.java
│   └── JwtAuthFilter.java
├── repository/                  # Spring Data JPA repositories
├── service/                     # Business logic
└── util/
    └── JwtUtil.java
```

---

## Frontend Integration

The React frontend expects `REACT_APP_API_URL=http://localhost:8080/api`.
All endpoints match the original Express contract — no frontend changes needed.
