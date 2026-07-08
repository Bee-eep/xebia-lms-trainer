# REST API Reference

The backend exposes a standard JSON REST API. All endpoints are prefixed with `/api/v1/trainer`.

## Endpoints

### 1. Categories & Courses
**`GET /categories`**
- Returns a list of all course categories (e.g., Programming, Soft Skills).

**`GET /courses`**
- Returns a list of all courses for the authenticated trainer.

**`GET /courses/{id}`**
- Returns a deeply nested `CourseDetailResponse` containing the full hierarchy: `Course` -> `Modules` -> `Submodules` -> `ContentBlocks`.

### 2. Modules & Submodules
**`POST /courses/{courseId}/modules`**
- Creates a new module (section) inside a course.
- **Payload:** `{ "title": "String", "sortOrder": Integer }`

**`POST /modules/{moduleId}/submodules`**
- Creates a new submodule (lesson) inside a module.
- **Payload:** `{ "title": "String", "estMinutes": Integer, "sortOrder": Integer }`

### 3. Content Blocks
**`POST /submodules/{submoduleId}/content`**
- Appends a new content block to a lesson.
- **Payload (`ContentForm`):** 
```json
{
  "type": "TEXT | HEADING | VIDEO | IMAGE",
  "body": "Markdown text goes here",
  "s3Key": "Optional local URL or S3 key for media",
  "headingLevel": "H1 | H2",
  "headingText": "Optional heading text"
}
```

**`PUT /submodules/{submoduleId}/content/reorder`**
- Reorders the content blocks within a lesson.
- **Payload:** `["uuid-1", "uuid-2", "uuid-3"]` (List of UUIDs in the new order)

### 4. File Uploads
**`POST /upload/local`**
- Handles local multipart file uploads.
- **Payload:** `multipart/form-data` with `file`.
- **Response:** `{ "url": "/uploads/filename.ext" }`
