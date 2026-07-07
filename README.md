# LMS Trainer Service

Welcome to the **LMS Trainer Service** repository. This Spring Boot application enables trainers to create, manage, publish, and evaluate learning content within the Learning Management System (LMS).

---

# Overview

The Trainer Service provides complete course authoring capabilities.

A trainer can:

- Create and manage Categories
- Create Courses under Categories
- Organize Courses into Modules
- Create Submodules (Lessons)
- Add multiple Content Blocks
- Upload media using AWS S3 Presigned URLs
- Publish Courses
- Review AI-generated evaluations and override scores

---

# Technology Stack

## Backend

- Java 21
- Spring Boot 3.3.4
- Spring MVC
- Spring Data JPA
- Spring Validation
- Spring Security
- PostgreSQL
- H2 Database
- Flyway
- AWS S3 SDK

---

# Project Structure

```
src
├── authoring
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
│
├── evaluation
│   ├── controller
│   ├── dto
│   ├── repository
│   └── service
│
├── common
│   ├── event
│   └── exception
│
└── config
```

---

# Entity Hierarchy

```
Category
│
└── Course
    │
    └── Module
        │
        └── Submodule
            │
            └── Content
```

---

# Features

## Category Management

- Create Category
- Update Category
- View Category
- List Categories
- Activate / Deactivate Category
- Delete Category

### Business Rule

A Category **cannot be deleted** if one or more Courses belong to it.

---

## Course Management

- Create Course
- Update Course
- Delete Course
- Publish Course
- View Course Details

---

## Module Management

- Create Module
- Update Module
- Delete Module

---

## Submodule Management

- Create Submodule
- Update Submodule
- Delete Submodule

---

## Content Management

Supported Content Types

- TEXT
- HEADING
- CODE
- IMAGE
- VIDEO
- LINK
- QUOTE
- TABLE
- BULLETS
- NUMBERED_LIST
- ARROW_LIST
- CALLOUT
- COMPARISON
- DIVIDER

Operations

- Create Content
- Update Content
- Delete Content
- Generate AWS S3 Presigned Upload URL

---

# Authoring Workflow

```
Create Category
       │
       ▼
Create Course
       │
       ▼
Add Modules
       │
       ▼
Add Submodules
       │
       ▼
Add Content Blocks
       │
       ▼
Review Course
       │
       ▼
Publish Course
       │
       ▼
Available to Learners
```

## Workflow Description

1. Create a **Category**.
2. Create a **Course** under that Category.
3. Add one or more **Modules**.
4. Add **Submodules** inside Modules.
5. Add learning **Content Blocks**.
6. Review the complete course.
7. Publish the Course.
8. Learners can access the published course.

---

# AI Evaluation Workflow

```
Learner Submits Assessment
            │
            ▼
AI Evaluates Submission
            │
            ▼
Trainer Reviews Evaluation
            │
            ▼
Override Score (Optional)
            │
            ▼
Add Trainer Comments
            │
            ▼
Save Final Evaluation
```

## AI Evaluation Features

- View AI-generated evaluations
- Review learner responses
- Review AI feedback
- Override AI-generated scores
- Add trainer comments
- Save final evaluation
- Maintain evaluation history

---

# Running the Application

## Development (H2)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

H2 Console

```
http://localhost:8080/h2-console
```

---

## Production (PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

# REST APIs

## Category APIs

| Method | Endpoint |
|----------|-----------------------------------------------|
| POST | `/api/v1/trainer/categories` |
| GET | `/api/v1/trainer/categories` |
| GET | `/api/v1/trainer/categories/{id}` |
| PUT | `/api/v1/trainer/categories/{id}` |
| PATCH | `/api/v1/trainer/categories/{id}/status` |
| DELETE | `/api/v1/trainer/categories/{id}` |

---

## Course APIs

| Method | Endpoint |
|----------|-----------------------------------------------|
| POST | `/api/v1/trainer/courses` |
| GET | `/api/v1/trainer/courses` |
| GET | `/api/v1/trainer/courses/{id}` |
| PUT | `/api/v1/trainer/courses/{id}` |
| DELETE | `/api/v1/trainer/courses/{id}` |
| POST | `/api/v1/trainer/courses/{id}/publish` |

---

## Module APIs

| Method | Endpoint |
|----------|-----------------------------------------------------------|
| POST | `/api/v1/trainer/courses/{courseId}/modules` |
| PUT | `/api/v1/trainer/modules/{id}` |
| DELETE | `/api/v1/trainer/modules/{id}` |

---

## Submodule APIs

| Method | Endpoint |
|----------|--------------------------------------------------------------|
| POST | `/api/v1/trainer/modules/{moduleId}/submodules` |
| PUT | `/api/v1/trainer/submodules/{id}` |
| DELETE | `/api/v1/trainer/submodules/{id}` |

---

## Content APIs

| Method | Endpoint |
|----------|---------------------------------------------------------------|
| POST | `/api/v1/trainer/submodules/{submoduleId}/content` |
| PUT | `/api/v1/trainer/content/{id}` |
| DELETE | `/api/v1/trainer/content/{id}` |
| POST | `/api/v1/trainer/content/media/presign` |

---

## AI Evaluation APIs

| Method | Endpoint |
|----------|-------------------------------------------|
| GET | `/api/v1/trainer/evaluations` |
| POST | `/api/v1/trainer/evaluations` |

---

# Business Rules

- Every Category can contain multiple Courses.
- Every Course belongs to exactly one Category.
- Every Module belongs to exactly one Course.
- Every Submodule belongs to exactly one Module.
- Every Content Block belongs to exactly one Submodule.
- Every Course starts in **DRAFT** status.
- Only **DRAFT** Courses can be published.
- Publishing changes the Course status to **PUBLISHED**.
- Categories containing one or more Courses **cannot be deleted**.
- Deleting a Course also deletes its Modules, Submodules, and Content.
- AI-generated scores can be overridden only by a Trainer.

---

# Database

Supported Databases

- H2 (Development)
- PostgreSQL (Production)

Database schema is managed using **Flyway** migrations.

---

