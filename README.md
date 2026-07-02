# LMS Trainer Service (`lms-trainer`)

Welcome to the **LMS Trainer Service** repository. This Spring Boot service handles authoring, evaluation, and operational workflows for the trainer console of the Learning Management System (LMS).

This guide describes how to configure your local development environment to connect to the shared PostgreSQL database server.

---

## Prerequisites

Before starting, ensure you have the following installed on your system:
1. **Java JDK 17** or higher.
2. **Git**.
3. **Maven** (optional, if building via command line).

---

## 1. Setup Environment Configurations (Database Link)

All developers in the project connect to a **shared PostgreSQL database instance**. You must configure the connection URL and credentials locally via environment variables.

1. In the project root, copy the `.env.example` template:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in the connection details of the shared PostgreSQL database:
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://<shared-db-host>:<port>/<database-name>
   SPRING_DATASOURCE_USERNAME=<your-shared-db-username>
   SPRING_DATASOURCE_PASSWORD=<your-shared-db-password>
   ```
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
