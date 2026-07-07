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

## 1. Setup & Environment Configurations

The application reads database connection settings directly from a `.env` file in the project root. Spring Boot automatically imports this file at startup via `spring.config.import`.

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

---

## 2. Running the Application

Since `.env` is loaded automatically by Spring Boot, you can run the server with a simple command:

* **Windows:**
  ```cmd
  .\mvnw.cmd spring-boot:run
  ```
* **macOS / Linux:**
  ```bash
  ./mvnw spring-boot:run
  ```

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

### Course Authoring (`CourseController.java`)
* `GET /api/v1/trainer/courses` — List all courses.
* `GET /api/v1/trainer/courses/{id}` — Retrieve full nested detail hierarchy tree of a course.
* `POST /api/v1/trainer/courses` — Create a new `DRAFT` course.
* `PUT /api/v1/trainer/courses/{id}` — Edit draft course metadata.
* `DELETE /api/v1/trainer/courses/{id}` — Delete a draft course and its modules cascade-style.
* `POST /api/v1/trainer/courses/{id}/publish` — Publish course (status -> `PUBLISHED`, version -> locked).

### Course Modules (`ModuleController.java`)
* `POST /api/v1/trainer/courses/{courseId}/modules` — Add module.

### Submodules / Lessons (`SubmoduleController.java`)
* `POST /api/v1/trainer/modules/{moduleId}/submodules` — Add submodule.

### Content Blocks (`ContentController.java`)
* `POST /api/v1/trainer/submodules/{submoduleId}/content` — Add content block.
* `POST /api/v1/trainer/content/media/presign` — Generate presigned S3 PUT URL for files.

### AI Evaluations (`EvaluationController.java`)
* `GET /api/v1/trainer/evaluations` — Retrieve all saved grade overrides.
* `POST /api/v1/trainer/evaluations` — Review and save a trainer score override.