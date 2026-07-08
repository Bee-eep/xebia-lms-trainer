# System Architecture & Data Model

## High-Level Architecture
The application is a monolith written in Spring Boot. It uses a Controller-Service-Repository pattern.

```mermaid
graph TD
    Client[Browser / Vanilla JS] -->|REST API & HTML| Controller(Spring REST Controllers)
    Controller --> Service(Spring Services)
    Service --> Repository(Spring Data JPA)
    Repository --> Database[(H2 In-Memory Database)]
    Controller --> Storage(Local File System /uploads/)
```

## Data Model (Entity Hierarchy)

The core domain model focuses on course authoring and content structure. 

```mermaid
erDiagram
    COURSE ||--o{ MODULE : contains
    MODULE ||--o{ SUBMODULE : contains
    SUBMODULE ||--o{ CONTENT : contains
    CATEGORY ||--o{ COURSE : groups

    COURSE {
        UUID course_id
        String title
        String summary
        Enum level
        Enum status
    }
    
    MODULE {
        UUID module_id
        String title
        Integer sort_order
    }
    
    SUBMODULE {
        UUID submodule_id
        String title
        Integer sort_order
        Integer est_minutes
    }
    
    CONTENT {
        UUID content_id
        Enum type
        String body
        String s3_key
        String url
        Integer sort_order
        String heading_level
        String heading_text
    }
```

### Content Blocks (`Content.java`)
The `Content` entity represents the fundamental building block of a lesson (Submodule). A content block has a specific `type` enum (e.g., `TEXT`, `HEADING`, `QUOTE`, `CODE`, `IMAGE`, `VIDEO`, `PDF`). Depending on the type, different fields are populated.
- For textual content, the `body` field stores Markdown strings.
- For media content, `url` and `s3_key` store the locations of the uploaded files.

## Local Storage Integration
The application supports both direct-to-S3 uploads and local storage fallbacks. 
- **Upload Controller:** `LocalUploadController` writes `MultipartFile` payloads directly to the `./uploads/` directory on the server.
- **Serving Files:** `WebConfig` registers a `ResourceHandler` to statically serve files from the `./uploads/` directory to the `/uploads/**` path on the frontend.
