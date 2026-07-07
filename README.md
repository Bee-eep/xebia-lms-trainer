# LMS Trainer Service (`lms-trainer`)

Welcome to the **LMS Trainer Service** repository. This Spring Boot service handles course authoring, assessment evaluation, and operational workflows for the trainer console of the Learning Management System (LMS).

---

## Architecture & Tech Stack

### Backend
1. **Core:** Java 21 / Spring Boot 3.3.4 (MVC, JPA, Validation, Security).
2. **Database:** Shared PostgreSQL instance (e.g. cloud host or Supabase) with schema migrations managed by **Flyway**.
3. **Media Uploads:** Client-side S3 upload facilitated by pre-signed upload URLs generated via the AWS S3 SDK.

### Frontend
1. **Console UI:** Single Page Application (SPA) powered by standard HTML5, CSS3, and Vanilla JavaScript.
2. **Design Tokens:** Modern, dark-themed dashboard panel layout. All rounded borders are configured with sharp (0px) corners as per design constraints.
3. **Internal Scrolling:** Flexbox constraints (`flex: 1; min-height: 0; overflow: hidden;`) ensure that workspace content areas scroll internally without breaking the layout context.

---

## Project Directory Structure

```
xebia-lms-trainer/
├── .env.example                       # Safe credential template (copy to .env)
├── .gitignore
├── DB_SETUP.md
├── README.md
├── pom.xml                            # Maven build config (Java 21, Spring Boot 3.3.4)
├── mvnw / mvnw.cmd / .mvn/           # Maven wrapper
│
└── src/
    ├── main/
    │   ├── java/com/xebia/lms/trainer/
    │   │   ├── TrainerApplication.java           # Spring Boot entry point
    │   │   │
    │   │   ├── authoring/                        # ── Course Authoring Domain ──
    │   │   │   ├── controller/
    │   │   │   │   ├── ContentController.java    #   Module/Submodule/Content + media presign routes
    │   │   │   │   └── CourseController.java     #   Course CRUD + publish routes
    │   │   │   ├── dto/
    │   │   │   │   ├── course/                   #   CourseForm, CourseResponse, CourseDetailResponse
    │   │   │   │   ├── module/                   #   ModuleForm, ModuleResponse, ModuleDetailResponse
    │   │   │   │   ├── submodule/                #   SubmoduleForm, SubmoduleResponse, SubmoduleDetailResponse
    │   │   │   │   └── content/                  #   ContentForm, ContentResponse, ContentDetailResponse,
    │   │   │   │                                 #   PresignedUploadResponse
    │   │   │   ├── model/                        #   JPA entities: Course, CourseModule, Submodule,
    │   │   │   │                                 #   Content, ContentType, CourseLevel, CourseStatus
    │   │   │   ├── repository/                   #   Spring Data JPA repositories
    │   │   │   └── service/
    │   │   │       ├── AuthoringService.java     #   Course lifecycle (create → build → publish)
    │   │   │       └── ContentMediaService.java  #   S3 presigned URL generation
    │   │   │
    │   │   ├── evaluation/                       # ── Evaluation Domain ──
    │   │   │   ├── controller/
    │   │   │   │   └── EvaluationController.java #   Trainer grade override routes
    │   │   │   ├── dto/
    │   │   │   │   ├── EvaluationForm.java       #   Override submission payload
    │   │   │   │   └── EvaluationResponse.java   #   Override response payload
    │   │   │   ├── model/
    │   │   │   │   └── TrainerEvaluation.java    #   JPA entity for trainer_evaluation table
    │   │   │   ├── repository/
    │   │   │   │   └── EvaluationRepository.java
    │   │   │   └── service/
    │   │   │       └── EvaluationService.java
    │   │   │
    │   │   ├── common/                           # ── Shared Infrastructure ──
    │   │   │   ├── event/
    │   │   │   │   └── CoursePublishedEvent.java  #   Domain event fired on publish
    │   │   │   └── exception/
    │   │   │       ├── GlobalExceptionHandler.java
    │   │   │       ├── InvalidCourseStateException.java
    │   │   │       └── ResourceNotFoundException.java
    │   │   │
    │   │   └── config/                           # ── Configuration ──
    │   │       ├── S3Config.java                 #   AWS S3 presigner bean
    │   │       └── SecurityConfig.java           #   Phase-1 security chain (open, stateless)
    │   │
    │   └── resources/
    │       ├── application.yml                   # Shared config (app name, port, S3)
    │       ├── application-dev.yml               # Dev profile: embedded H2, ddl-auto: update
    │       ├── application-prod.yml              # Prod profile: PostgreSQL, Flyway, ddl-auto: validate
    │       ├── certs/                            # TLS certificates (placeholder)
    │       ├── db/migration/                     # Flyway SQL migrations
    │       │   ├── V1__init_course_schema.sql
    │       │   └── V2__create_evaluation_schema.sql
    │       └── static/                           # Frontend SPA
    │           ├── index.html
    │           ├── app.js
    │           └── app.css
    │
    └── test/
        └── java/com/xebia/lms/trainer/          # Test root (mirrors main structure)
```

### Package Design Rationale

The Java source follows a **domain-driven package layout**:

| Package | Responsibility |
|---------|---------------|
| `authoring` | Course creation, module/submodule/content management, media uploads, publishing |
| `evaluation` | Trainer-side grade overrides of AI-scored learner submissions |
| `common` | Cross-cutting concerns: domain events, global exception handling |
| `config` | Spring `@Configuration` beans (security, S3 presigner) |

DTOs are further grouped by **domain concept** (`dto/course/`, `dto/module/`, `dto/submodule/`, `dto/content/`) to keep the authoring package navigable as it grows.

---

## 1. Setup & Environment Configurations

The application supports two Spring profiles:

| Profile | Database | Use Case |
|---------|----------|----------|
| `dev` | Embedded H2 (PostgreSQL mode) | Local development, no external DB needed |
| `prod` | PostgreSQL via env vars | Staging / production deployments |

### Quick Start (Dev — H2)

No `.env` file needed. Just run with the `dev` profile:

```bash
# macOS / Linux
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/lms_trainer`).

### Production Setup (PostgreSQL)

1. Copy the `.env.example` file to create your `.env` file:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in the connection details of your shared PostgreSQL database:
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://<shared-db-host>:<port>/<database-name>?sslmode=require
   SPRING_DATASOURCE_USERNAME=<username>
   SPRING_DATASOURCE_PASSWORD=<password>
   ```

   > [!IMPORTANT]
   > Do NOT commit the `.env` file containing database credentials to version control. It is ignored by Git.

3. Run with the `prod` profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
   ```

---

## 2. Running the Application

Once the terminal prints `Started TrainerApplication`, open your web browser and navigate to:
```url
http://localhost:8080/index.html
```

---

## 3. Site Workflows & Features

The web interface is structured into a dashboard with a **Left Navigation Sidebar** to switch between features:

### A. Course Authoring Workflow
1. **Create Course:** Click **+ New Course** in the sidebar. Fill in the title, description, and difficulty level. This starts a course in the `DRAFT` status under your Trainer ID.
2. **Add Sections (Modules):** Click **+ Add Section** inside the course workspace. Sorteable sections structure the syllabus.
3. **Add Lessons (Submodules):** Inside any section, click **+ Add Lesson** to define submodules with custom estimated reading times.
4. **Attach Content Blocks:** Click **+ Add Content** inside a lesson. Select the content type:
   * **Text:** Enter plain markdown text.
   * **Code:** Write code snippets with syntax language highlighting (Java, SQL, JavaScript, HTML, CSS).
   * **Media (Image, PDF, Video):** Upload files directly. The client requests a pre-signed URL from the backend, uploads it directly to the S3 bucket, and stores the S3 key reference.
5. **Edit Course Info:** Click **Edit Info** to modify the title, summary, or difficulty level of any draft course.
6. **Remove Course:** Click **Delete** in the draft course header. This performs a cascading database cleanup to remove the course along with all associated modules, lessons, and content blocks.
7. **Publish Course:** Once ready, click **Publish Course**. This freezes all editing features, transitions the status to `PUBLISHED`, and fires a `CoursePublishedEvent` for enrolling learners.

### B. AI Score Evaluations Workflow
1. **View Submissions Roster:** Click **AI Evaluations** in the main navigation sidebar. The panel displays a roster of student quiz submissions.
2. **Review Answers:** Select a student (e.g. Alice Johnson) to render their complete answer sheet, the questions asked, and the detailed AI feedback/scores.
3. **Override Score:** If the AI score is inaccurate, type an updated grade in the **Override Score** field (0-100), write trainer grading comments, and click **Save Evaluation Override**.
4. **View Override History:** The evaluation override is persisted in the `trainer_evaluation` database table. The history log at the bottom updates to list all previously recorded overrides in real-time.

---

## 4. API Endpoints Reference

### Course Authoring (`authoring/controller/CourseController.java`)
* `GET /api/v1/trainer/courses` — List all courses.
* `GET /api/v1/trainer/courses/{id}` — Retrieve full nested detail hierarchy tree of a course.
* `POST /api/v1/trainer/courses` — Create a new `DRAFT` course.
* `PUT /api/v1/trainer/courses/{id}` — Edit draft course metadata.
* `DELETE /api/v1/trainer/courses/{id}` — Delete a draft course and its modules cascade-style.
* `POST /api/v1/trainer/courses/{id}/publish` — Publish course (status -> `PUBLISHED`, version -> locked).

### Content & Modules (`authoring/controller/ContentController.java`)
* `POST /api/v1/trainer/courses/{courseId}/modules` — Add module.
* `PUT /api/v1/trainer/modules/{id}` — Update module.
* `DELETE /api/v1/trainer/modules/{id}` — Delete module.
* `POST /api/v1/trainer/modules/{moduleId}/submodules` — Add submodule.
* `PUT /api/v1/trainer/submodules/{id}` — Update submodule.
* `DELETE /api/v1/trainer/submodules/{id}` — Delete submodule.
* `POST /api/v1/trainer/submodules/{submoduleId}/content` — Add content block.
* `PUT /api/v1/trainer/content/{id}` — Update content block.
* `DELETE /api/v1/trainer/content/{id}` — Delete content block.
* `POST /api/v1/trainer/content/media/presign` — Generate presigned S3 PUT URL for files.

### AI Evaluations (`evaluation/controller/EvaluationController.java`)
* `GET /api/v1/trainer/evaluations` — Retrieve all saved grade overrides.
* `POST /api/v1/trainer/evaluations` — Review and save a trainer score override.