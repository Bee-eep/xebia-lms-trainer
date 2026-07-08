# Xebia LMS Trainer - Code Wiki

Welcome to the Xebia LMS Trainer Wiki! This documentation is designed to help developers understand the architecture, data models, APIs, and frontend logic of the application.

## Overview
Xebia LMS Trainer is a Learning Management System designed to allow trainers to author courses, manage media content (images, PDFs, videos), and structure learning material into organized modules and submodules (lessons).

The system uses a monolithic architecture, exposing a REST API from the backend, which is consumed by a pure, dependency-free Vanilla Javascript frontend.

## Technology Stack
- **Backend:** Java, Spring Boot 3
- **Database:** H2 (In-Memory) / Spring Data JPA
- **Frontend Framework:** Vanilla HTML, CSS (Custom Design System), JavaScript (ES6 Modules)
- **Markdown Parsing:** `marked.js`
- **File Storage:** Local File Storage (Configured for direct uploads without S3)

## Quick Start
To build and run the backend server:

```bash
# Ensure you are at the project root
.\mvnw.cmd spring-boot:run
```
The server will start on port `8080` (or `8081` if `8080` is in use).

Access the frontend applications via your browser:
- **Authoring Dashboard:** `http://localhost:8080/index.html`
- **Lesson Viewer:** `http://localhost:8080/viewer.html`

## Wiki Navigation
Please refer to the following documents for detailed insights into the specific components of the application:

- [Architecture & Data Model](Architecture.md)
- [Frontend Logic & Styling](Frontend.md)
- [REST API Reference](API-Reference.md)
