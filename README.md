# My Website

Personal portfolio and contact platform built with Spring Boot.

## Tech Stack

- **Backend:** Spring Boot 3.4.1, Java 21
- **Database:** MongoDB Atlas
- **Security:** Spring Security with JWT authentication
- **Email:** SendGrid (production), mock service (development)
- **Admin UI:** Thymeleaf + Bootstrap 5
- **Deployment:** Docker, Render

## Features

- REST API for projects, messages, and user management
- JWT-based authentication for API endpoints
- Admin dashboard with session-based login (Thymeleaf)
- Contact form with automatic email notifications
- CRUD management for projects and messages
- Automated MongoDB Atlas IP whitelist updates
- CORS configuration for Angular frontend integration
- Multi-profile support (dev, test, prod)

## API Endpoints

| Resource | Methods | Auth |
|----------|---------|------|
| `/auth/**` | POST | Public |
| `/projects/**` | GET, POST, PUT, DELETE | Public |
| `/messages/**` | GET, POST, PUT, DELETE | Public |
| `/users/**` | GET, POST, PUT, DELETE | JWT required |
| `/admin/**` | GET, POST | Session (ADMIN role) |

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MongoDB Atlas account

### Local Development

1. Copy the example config and fill in your values:
   ```
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```

2. Build and run:
   ```
   ./mvnw spring-boot:run
   ```

3. Open `http://localhost:8080`

### Running Tests

```
./mvnw test
```

### Docker

```
docker build -t my-website .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATA_MONGODB_URI=<your-uri> \
  -e JWT_SECRET=<your-secret> \
  -e JWT_EXPIRATION_MS=86400000 \
  -e SENDGRID_API_KEY=<your-key> \
  -e SITE_OWNER_EMAIL=<your-email> \
  -e ADMIN_EMAIL=<admin-email> \
  -e MONGO_API_PUBLIC_KEY=<key> \
  -e MONGO_API_PRIVATE_KEY=<key> \
  -e MONGO_PROJECT_ID=<id> \
  my-website
```

## Project Structure

```
src/main/java/dev/lilianagorga/mywebsite/
├── config/          Security, MongoDB, JWT, CORS, scheduling
├── controller/      REST controllers + Admin controller
├── entity/          User, Project, Message
├── repository/      MongoDB repositories
├── service/         Business logic, email, notifications
└── request/response DTOs
```

## Environment Variables (Production)

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | Must be `prod` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string |
| `JWT_SECRET` | Base64-encoded JWT signing key |
| `JWT_EXPIRATION_MS` | Token expiration in milliseconds |
| `SENDGRID_API_KEY` | SendGrid API key |
| `SITE_OWNER_EMAIL` | Sender email address |
| `ADMIN_EMAIL` | Admin notification email |
| `MONGO_API_PUBLIC_KEY` | MongoDB Atlas API public key |
| `MONGO_API_PRIVATE_KEY` | MongoDB Atlas API private key |
| `MONGO_PROJECT_ID` | MongoDB Atlas project ID |