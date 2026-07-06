# LMS Trainer Service (`lms-trainer`)

Welcome to the **LMS Trainer Service** repository. This Spring Boot service handles authoring, evaluation, and operational workflows for the trainer console of the Learning Management System (LMS).

This guide describes how to configure your local development environment to connect to the shared PostgreSQL database server.

---

## Scope (Phase 1)

This build covers **Course Authoring only** — Course → Module → Submodule → Content, plus publish and media presign. It matches the Course Authoring & Trainer Workspace (Module 04) blueprint. Evaluation, Tickets, and Schedule are not built yet.

---

## Prerequisites

Before starting, ensure you have the following installed on your system:
1. **Java JDK 17** or higher (the service targets Java 21).
2. **Git**.
3. **Maven** (optional, if building via command line).

---

## Dependencies

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-parent` (3.3.4) | Manages compatible versions for every dependency below. |
| `spring-boot-starter-web` | Embedded Tomcat + Spring MVC — powers the trainer REST controllers. |
| `spring-boot-starter-validation` | Enables `@Valid` + `@NotBlank`/`@NotNull`/`@Size` on request DTOs. |
| `spring-boot-starter-data-jpa` | Hibernate + Spring Data repositories. |
| `postgresql` | JDBC driver Hibernate uses to talk to Postgres (including Supabase). |
| `flyway-core` | Runs migration scripts and tracks which have been applied. |
| `flyway-database-postgresql` | Postgres-specific Flyway support (required from Flyway 10+). |
| `spring-boot-starter-security` | Backs the security filter chain; the entry point for JWT RBAC in Phase 2. |
| `software.amazon.awssdk:s3` | `S3Presigner` used to generate signed content-media upload URLs. |
| `lombok` | Generates getters/setters on entities so they stay short. |
| `spring-boot-starter-test` | JUnit 5 + Mockito + Spring test support (test scope only). |

---

## 1. Setup Environment Configurations (Database Link)

All developers in the project connect to a **shared PostgreSQL database instance**. You must configure the connection URL and credentials locally via environment variables.

1. In the project root, copy the `.env.example` template:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in the connection details of the shared PostgreSQL database:
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://<shared-db-host>:<port>/<database-name>?sslmode=require
   SPRING_DATASOURCE_USERNAME=<your-shared-db-username>
   SPRING_DATASOURCE_PASSWORD=<your-shared-db-password>
   ```

   > [!NOTE]
   > **Supabase Connection Note:** The raw URI provided by Supabase uses the `postgresql://` prefix and embeds credentials. You must:
   > 1. Change the prefix to `jdbc:postgresql://` (e.g. `jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require`).
   > 2. Separate the username (`postgres`) and password into the dedicated variables.

   > [!IMPORTANT]
   > Do NOT commit the `.env` file containing the shared database credentials to version control. It is already included in `.gitignore`.

---

## 2. Running the Spring Boot Application

Once the database configuration environment variables are loaded in your context:

### A. Run via command line (Maven Wrapper / Local Maven)
Use Maven to clean and run the Spring Boot application:
```bash
mvn spring-boot:run
```

### B. Run via IDE (IntelliJ IDEA / VS Code / Eclipse)
1. Open the project root folder in your IDE.
2. Ensure you have an **EnvFile** or similar plugin installed (e.g. *EnvFile* in IntelliJ) to load variables from the `.env` file into your local run configuration.
3. Select `TrainerApplication.java` as the entry point class and run/debug.

---

## 3. Database Migrations

Schema migrations are handled automatically using **Flyway** on application startup:
1. Migration scripts are located in `src/main/resources/db/migration`.
2. When the Spring Boot application starts, it will automatically apply any new migrations to the shared database.

---

## 4. API Flow (Postman)

1. `POST /api/v1/trainer/courses` with header `X-Trainer-Id: <uuid>` → capture `courseId`.
2. `POST /api/v1/trainer/courses/{courseId}/modules` → capture `moduleId`.
3. `POST /api/v1/trainer/modules/{moduleId}/submodules` → capture `submoduleId`.
4. `POST /api/v1/trainer/submodules/{submoduleId}/content`.
5. `POST /api/v1/trainer/courses/{courseId}/publish`.

For IMAGE/PDF/VIDEO content, call
`POST /api/v1/trainer/content/media/presign?fileName=x&contentType=y`
first, upload to the returned `uploadUrl`, then use the returned `s3Key`
in step 4.

---

## 5. Not Yet Wired (Phase 2)

- JWT RS256 verification + dynamic `TRN:COURSE:MANAGE` RBAC (the security config currently permits all requests).
- Course versioning on edit-after-publish (copy-on-write to version N+1).