# Fitness Monolith

A Spring Boot REST API for tracking fitness activities, managing users, and generating activity-based recommendations. Built as a monolith with JWT authentication, PostgreSQL persistence, and auto-generated OpenAPI/Swagger docs.

## 🚀 Live Demo

The API is deployed and publicly accessible on Render:

**https://fitness-monolith-v2-9s35.onrender.com**

> Note: Free Render web services spin down after periods of inactivity, so the first request after a while may take 30–60 seconds to respond while the instance wakes up.

Interactive API docs (Swagger UI) on the live instance:
**https://fitness-monolith-v2-9s35.onrender.com/swagger-ui.html**

## Table of Contents

- [Overview & Features](#overview--features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Local Setup](#local-setup)
  - [Clone the repository](#clone-the-repository)
  - [Configure environment variables](#configure-environment-variables)
  - [Build](#build)
  - [Test](#test)
  - [Run](#run)
- [Docker](#docker)
  - [Build the image](#build-the-image)
  - [Run the container](#run-the-container)
  - [Logs, stop, remove, rebuild](#logs-stop-remove-rebuild)
  - [Docker Compose (optional)](#docker-compose-optional)
- [Database](#database)
- [API Documentation](#api-documentation)
- [Render Deployment Guide](#render-deployment-guide)
- [Other Cloud Deployment Options](#other-cloud-deployment-options)
- [Production Recommendations](#production-recommendations)
- [Troubleshooting](#troubleshooting)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Overview & Features

Fitness Monolith exposes a JSON REST API for:

- **User accounts** — register and log in, with passwords hashed via BCrypt and `USER` / `ADMIN` roles.
- **JWT authentication** — login issues a signed JWT (HS256, 48-hour expiry) that must be sent on subsequent requests.
- **Activity tracking** — log activities (`RUNNING`, `WALKING`, `CYCLING`, `JOGGING`) with duration, calories burned, start time, and arbitrary extra metrics (stored as JSON), and list a user's activities.
- **Recommendations** — generate and retrieve recommendations (improvements, suggestions, safety notes) tied to a user and/or an activity.
- **Auto-managed timestamps** — `createdAt` / `updatedAt` are stamped automatically by Hibernate on every entity.
- **Self-documenting API** — OpenAPI schema and Swagger UI are generated automatically from the controllers.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Web | Spring Web MVC |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL (driver: `org.postgresql:postgresql`) |
| Security | Spring Security + JJWT 0.13.0 (JWT auth) |
| API Docs | springdoc-openapi (`springdoc-openapi-starter-webmvc-ui` 3.0.3) — Swagger UI / OpenAPI 3 |
| Boilerplate | Lombok |
| JSON | Jackson |
| Build | Maven (Maven Wrapper included, Maven 3.9.16) |
| Container image | Spring Boot's Cloud Native Buildpacks plugin (`spring-boot-maven-plugin`) |

> The project also ships the `mysql-connector-j` driver in `pom.xml`, but it is currently unused — the active Hibernate dialect is `PostgreSQLDialect` (the MySQL dialect line is commented out in `application.properties`).

## Prerequisites

- **JDK 21** (matches `<java.version>21</java.version>` in `pom.xml`)
- **Maven** — not required to install separately; use the included wrapper (`./mvnw`)
- **A PostgreSQL database** — a managed instance such as [Neon](https://neon.tech) works well (the project's own commit history shows it deployed against Neon), or a local Postgres instance
- **Git**
- **Docker** (optional, only needed for the [Docker](#docker) workflow)

## Local Setup

### Clone the repository

```bash
git clone <this-repository-url>
cd fitness-monolith
```

### Configure environment variables

`application.properties` reads three required environment variables — there are no defaults, so the app will fail to start without them:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC URL of your PostgreSQL database | `jdbc:postgresql://<host>:5432/<db>?sslmode=require` |
| `DB_USERNAME` | Database username | `fitness_user` |
| `DB_PASSWORD` | Database password | `••••••••` |

There is no `.env`-loading dependency in this project, so export these in your shell (or set them in your IDE's run configuration) before building/running:

```bash
export DB_URL="jdbc:postgresql://<host>:5432/<db>?sslmode=require"
export DB_USERNAME="your_db_user"
export DB_PASSWORD="your_db_password"
```

### Build

```bash
./mvnw clean install
```

### Test

```bash
./mvnw test
```

### Run

```bash
./mvnw spring-boot:run
```

or, after building the jar:

```bash
java -jar target/fitness-monolith-0.0.1-SNAPSHOT.jar
```

The app starts on **port 8080** by default (no `server.port` override is set in `application.properties`), so it will be reachable at `http://localhost:8080`.

## Docker

There is no `Dockerfile` checked into this repository. Instead, `pom.xml` configures the Spring Boot Maven plugin's image builder (Cloud Native Buildpacks), which builds a ready-to-run OCI image directly from the compiled app:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <image>
            <name>docker.io/atharvaparkale/fitness-monolith:latest</name>
            <imagePlatform>linux/amd64</imagePlatform>
        </image>
    </configuration>
</plugin>
```

### Build the image

Requires Docker running locally:

```bash
./mvnw spring-boot:build-image
```

This produces the image `docker.io/atharvaparkale/fitness-monolith:latest`.

### Run the container

```bash
docker run -d \
  --name fitness-monolith \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://<host>:5432/<db>?sslmode=require" \
  -e DB_USERNAME="your_db_user" \
  -e DB_PASSWORD="your_db_password" \
  docker.io/atharvaparkale/fitness-monolith:latest
```

- **Port mapping**: `-p 8080:8080` — the app listens on 8080 inside the container (no `server.port` override), so map it to whichever host port you prefer, e.g. `-p 9090:8080`.
- **Environment variables**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` are required, as above.
- **Volumes**: none are needed — the app is stateless; all persistent data lives in the external PostgreSQL database, not on the container's filesystem.

### Logs, stop, remove, rebuild

```bash
# Follow logs
docker logs -f fitness-monolith

# Stop the container
docker stop fitness-monolith

# Remove the container
docker rm fitness-monolith

# Rebuild the image after code changes
./mvnw spring-boot:build-image

# Then re-run (remove the old container first if it still exists)
docker rm -f fitness-monolith
docker run -d --name fitness-monolith -p 8080:8080 \
  -e DB_URL="..." -e DB_USERNAME="..." -e DB_PASSWORD="..." \
  docker.io/atharvaparkale/fitness-monolith:latest
```

### Docker Compose (optional)

No `docker-compose.yml` exists in the repository. If you want one for local development (app + a local Postgres database), the following uses only the environment variables the app already reads (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) — save it as `docker-compose.yml` at the project root:

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: fitness
      POSTGRES_USER: fitness_user
      POSTGRES_PASSWORD: fitness_pass
    ports:
      - "5432:5432"
    volumes:
      - fitness_pgdata:/var/lib/postgresql/data

  app:
    image: docker.io/atharvaparkale/fitness-monolith:latest
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/fitness
      DB_USERNAME: fitness_user
      DB_PASSWORD: fitness_pass

volumes:
  fitness_pgdata:
```

Build the app image first (`./mvnw spring-boot:build-image`), then:

```bash
docker compose up -d      # start
docker compose logs -f    # logs
docker compose down       # stop and remove
```

## Database

- **Engine**: PostgreSQL. The active Hibernate dialect is `org.hibernate.dialect.PostgreSQLDialect` (see `application.properties`).
- **Schema management**: there is **no Flyway or Liquibase** in this project. Hibernate manages the schema automatically via:
  ```properties
  spring.jpa.hibernate.ddl-auto=update
  ```
  Tables (`fitness_user`, `activity`, `recommendation`) are created/altered automatically at startup based on the JPA entities. There are no manual migration scripts to run.
- **Seed data**: none is included in the repository — you'll need to register a user via the API to start using it.
- **Connection configuration**: set via the `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables (see [Configure environment variables](#configure-environment-variables)). For a serverless Postgres provider like Neon, a typical URL looks like:
  ```
  jdbc:postgresql://<project>-pooler.<region>.aws.neon.tech/<db>?sslmode=require
  ```

## API Documentation

- **Base URL (local)**: `http://localhost:8080`
- **Base URL (production)**: `https://fitness-monolith-v2-9s35.onrender.com`
- **Swagger UI**: `/swagger-ui.html` (springdoc redirects to `/swagger-ui/index.html`)
- **OpenAPI JSON**: `/v3/api-docs`

Both Swagger paths are explicitly allowed without authentication in `SecurityConfig`.

### Authentication

All endpoints except `/api/auth/**` and the Swagger/OpenAPI paths require a JWT bearer token (Spring Security's `anyRequest().authenticated()`). Endpoints under `/api/admin/**` additionally require the `ADMIN` role (this path is reserved in `SecurityConfig`; no admin controller is implemented yet).

Flow:
1. `POST /api/auth/register` to create a user.
2. `POST /api/auth/login` to receive a JWT.
3. Send the token as `Authorization: Bearer <token>` on subsequent requests.

The `GET /api/activities` endpoint additionally expects an `X-User-ID` header identifying the user whose activities to fetch.

### Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Log in, returns a JWT + user profile |
| POST | `/api/activities` | JWT | Track a new activity |
| GET | `/api/activities` | JWT + `X-User-ID` header | List a user's activities |
| POST | `/api/recommendations/generate` | JWT | Generate a recommendation |
| GET | `/api/recommendations/user/{userId}` | JWT | List recommendations for a user |
| GET | `/api/recommendations/activity/{activityId}` | JWT | List recommendations for an activity |

### Example requests

Register:

```bash
curl -X POST https://fitness-monolith-v2-9s35.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "changeme123",
    "firstName": "Jane",
    "lastName": "Doe",
    "role": "USER"
  }'
```

Login:

```bash
curl -X POST https://fitness-monolith-v2-9s35.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "changeme123"
  }'
```

Response includes a `token` — export it for the next calls:

```bash
export TOKEN="<token from login response>"
export USER_ID="<id from login response's user object>"
```

Track an activity:

```bash
curl -X POST https://fitness-monolith-v2-9s35.onrender.com/api/activities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": "'"$USER_ID"'",
    "type": "RUNNING",
    "duration": "30",
    "caloriesBurned": "300",
    "startTime": "2026-07-26T07:00:00",
    "additionalMetrics": { "distanceKm": 5 }
  }'
```

List activities:

```bash
curl https://fitness-monolith-v2-9s35.onrender.com/api/activities \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-User-ID: $USER_ID"
```

Generate a recommendation:

```bash
curl -X POST https://fitness-monolith-v2-9s35.onrender.com/api/recommendations/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": "'"$USER_ID"'",
    "activityId": "<activity id>",
    "improvements": ["Increase pace gradually"],
    "suggestions": ["Add interval training"],
    "safety": ["Stay hydrated"]
  }'
```

## Render Deployment Guide

This is how the [live demo](#-live-demo) is hosted, and how you'd redeploy your own copy on [Render](https://render.com):

1. **Create a Web Service** in Render and connect this repository.
2. **Build Command**:
   ```bash
   ./mvnw clean package -DskipTests
   ```
3. **Start Command**:
   ```bash
   java -jar target/fitness-monolith-0.0.1-SNAPSHOT.jar
   ```
4. **Java version**: Render needs to know to use JDK 21. If it isn't auto-detected from `pom.xml`, add a `system.properties` file at the repo root with:
   ```properties
   java.runtime.version=21
   ```
5. **Environment variables** — set these in the Render dashboard under *Environment*:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
6. **Port handling**: Render injects a `PORT` environment variable and expects the app to bind to it. This project's `application.properties` does **not** currently set `server.port`, so it defaults to `8080`. To make the app honor Render's assigned port, add to `application.properties`:
   ```properties
   server.port=${PORT:8080}
   ```
   (Spring Boot's relaxed binding also picks up an env var named `SERVER_PORT` if you'd rather set it directly in the Render dashboard instead of editing the properties file.)
7. **Database**: point `DB_URL` at a reachable managed Postgres instance (Neon, Render's own managed Postgres, or any other provider), including `?sslmode=require` if the provider requires SSL.
8. **Redeploy**: pushing to the connected branch triggers an automatic redeploy, or use *Manual Deploy → Deploy latest commit* in the Render dashboard.

Alternatively, since the project already builds a container image via Buildpacks (`./mvnw spring-boot:build-image`), you can create the Web Service as a **Docker**-type service pointing at a pushed image (e.g. `docker.io/atharvaparkale/fitness-monolith:latest`) instead of a native Java build.

## Other Cloud Deployment Options

These are optional alternatives; none of this configuration exists in the repo — treat as a starting point.

### AWS (Elastic Beanstalk / ECS)

- Build and push the image: `./mvnw spring-boot:build-image` then `docker push docker.io/atharvaparkale/fitness-monolith:latest` (or push to ECR after tagging).
- Deploy via Elastic Beanstalk's "Docker platform", or run it as an ECS Fargate service/task definition referencing the pushed image.
- Use Amazon RDS for PostgreSQL and set `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` as task/environment variables or via Secrets Manager.

### Railway

- `railway init` in the project directory, then `railway up` — Railway detects the Maven project and builds it (or builds the Buildpacks image).
- Set variables: `railway variables set DB_URL=... DB_USERNAME=... DB_PASSWORD=...`
- Railway also injects a `PORT` variable — apply the same `server.port=${PORT:8080}` change noted in the Render guide.

### Google Cloud Run

- Build the image with Buildpacks and push to Artifact Registry:
  ```bash
  ./mvnw spring-boot:build-image \
    -Dspring-boot.build-image.imageName=<region>-docker.pkg.dev/<project>/<repo>/fitness-monolith
  docker push <region>-docker.pkg.dev/<project>/<repo>/fitness-monolith
  ```
- Deploy:
  ```bash
  gcloud run deploy fitness-monolith \
    --image <region>-docker.pkg.dev/<project>/<repo>/fitness-monolith \
    --set-env-vars DB_URL=...,DB_USERNAME=...,DB_PASSWORD=... \
    --port 8080
  ```
- Cloud Run also sets `$PORT`; the same `server.port=${PORT:8080}` note applies.

### Azure (App Service for Containers / Container Apps)

- Push the built image to Azure Container Registry (ACR).
- Create an App Service for Containers (or a Container App) referencing the ACR image.
- Configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` as Application Settings / environment variables, and use Azure Database for PostgreSQL as the backing database.

## Production Recommendations

- **Profiles**: only a single `application.properties` exists today. Consider splitting into `application-dev.properties` / `application-prod.properties` and selecting one via `SPRING_PROFILES_ACTIVE`, so verbose local settings don't leak into production.
- **Secrets**: the JWT signing secret in `JwtUtils.java` is currently hardcoded in source. Externalize it (e.g. an env var read via `@Value`) before running this in production, and never reuse the checked-in secret.
- **HTTPS**: Render terminates TLS automatically for `*.onrender.com`; if you attach a custom domain, configure Render's managed certificates (or your provider's equivalent).
- **Health checks**: no Actuator dependency is included yet. Add `spring-boot-starter-actuator` and expose `/actuator/health` if you want Render (or any platform) to perform real health checks instead of just port-liveness.
- **Logging**: `spring.jpa.show-sql=true` and `logging.level.org.hibernate.SQL=DEBUG` are currently enabled globally in `application.properties` — this is very verbose for production and will leak query data into logs; tune these down for a prod profile.
- **Monitoring**: no APM/metrics integration is configured. Consider Render's built-in metrics plus an external tool (e.g. Grafana, Datadog) if you need deeper visibility.
- **CORS**: `SecurityConfig` does not configure CORS. If a browser-based frontend on a different origin will call this API, add a `CorsConfigurationSource` bean.

## Troubleshooting

- **App fails to start immediately**: `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` have no defaults in `application.properties` — the app will fail to create the datasource bean if any are missing.
- **`401 Unauthorized` on protected endpoints**: make sure you're sending `Authorization: Bearer <token>` with a token from `/api/auth/login`; tokens expire after 48 hours (`172800000` ms, see `JwtUtils`).
- **`GET /api/activities` returns nothing/errors**: this endpoint requires the `X-User-ID` header in addition to the bearer token.
- **Deployed app doesn't respond / platform reports failed health check**: confirm the app is bound to the port the platform expects (see the `server.port=${PORT:8080}` note in the [Render guide](#render-deployment-guide)).
- **First request to the live demo is slow**: Render's free tier spins down idle services; the first request wakes it back up.

## Project Structure

```
fitness-monolith/
├── pom.xml
├── mvnw, mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/project/fitness/
│   │   │   ├── FitnessMonolithApplication.java   # main entry point
│   │   │   ├── controller/                        # REST controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ActivityController.java
│   │   │   │   └── RecommendationController.java
│   │   │   ├── service/                           # business logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ActivityService.java
│   │   │   │   └── RecommendationService.java
│   │   │   ├── repository/                        # Spring Data JPA repositories
│   │   │   ├── model/                              # JPA entities & enums
│   │   │   ├── dto/                                # request/response payloads
│   │   │   └── security/                           # JWT filter, JwtUtils, SecurityConfig
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/project/fitness/
│           └── FitnessMonolithApplicationTests.java
```

## Contributing

This repository doesn't currently define a formal contribution process. If you'd like to contribute:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/my-change`).
3. Make your changes, adding/updating tests where relevant (`./mvnw test`).
4. Open a pull request describing the change.

## License

No license file is currently included in this repository. If you intend to open-source it, add a `LICENSE` file (e.g., MIT, Apache 2.0) to make the usage terms explicit.
