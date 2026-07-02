# Database Setup & Configuration Guide

This guide describes how to configure the PostgreSQL database for the `lms-trainer` application using a shared database instance.

## Overview

Instead of running individual local databases, all developers connect to a **shared PostgreSQL database** server. This ensures everyone develops against the same database schema, and Flyway handles migrations collaboratively.

---

## 1. Connection Configurations

The application reads database connection settings using the following environment variables:

| Environment Variable | Default Value | Description |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/lms_trainer` | JDBC connection URL for PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `password` | Database password |

### Local Environment File Setup (.env)
1. Copy the `.env.example` file in the root directory and rename it to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Open the `.env` file and replace the default values with the **shared database** details:
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://<shared-db-host>:<port>/<database-name>
   SPRING_DATASOURCE_USERNAME=<shared-username>
   SPRING_DATASOURCE_PASSWORD=<shared-password>
   ```
3. If using an IDE (e.g. IntelliJ IDEA, Eclipse, or VS Code), ensure your run configurations load the environment variables from this `.env` file, or install an environment file plugin (e.g., *EnvFile* for IntelliJ).

---

## 2. Shared Database Access & Interaction

To connect directly to the shared database server via graphical clients (like DBeaver, TablePlus, or pgAdmin):

1. Launch your SQL client.
2. Create a new PostgreSQL connection.
3. Supply the shared database host, port, database name, user, and password configured in your `.env`.

---

## 3. Run Migrations

Schema migrations are handled automatically using **Flyway** on application startup:
1. Migration scripts are located in `src/main/resources/db/migration`.
2. When the Spring Boot application boots up, Flyway will run any outstanding migrations.
3. **Important Notice for Collaborative Development:** Since the database is shared, make sure you coordinate migrations with other team members to avoid concurrent modification issues on the Flyway schema history table.
