# Frontend Architecture & Logic

The frontend of the Xebia LMS Trainer application is intentionally built without heavy Javascript frameworks like React or Angular. It relies entirely on Vanilla HTML, CSS, and modern ES6 Javascript.

## Application Structure

The frontend consists of two main pages, each with its own Javascript logic file.

### 1. `index.html` & `app.js` (Authoring Dashboard)
This represents the internal dashboard where trainers create and organize courses.
- **State Management**: Uses a global `state` object inside `app.js` (e.g., `state.selectedCourseId`, `state.categories`).
- **DOM Manipulation**: Functions explicitly modify the DOM (e.g., `document.getElementById('...')`) when data changes.
- **Modals**: Custom modal implementations using CSS class toggles (`.active`) for creating courses, modules, submodules, and content blocks.
- **Content Blocks**: Block reordering is handled via `moveContentUp` and `moveContentDown`, which update the list array and trigger a backend `PUT` request to update sort orders.

### 2. `viewer.html` & `viewer.js` (Lesson Viewer)
This represents the student-facing view where courses are consumed.
- **Dynamic Routing Simulation**: Uses URL Search Params (e.g., `?courseId=123`) to determine what course to load.
- **Sidebar Navigation**: Fetches course hierarchy and renders it recursively.
- **Content Rendering (`renderContentBlocks`)**: Iterates over a Submodule's `contentBlocks` array. Parses block types using a massive `switch` statement:
  - **TEXT / QUOTE / CODE**: Parsed using `marked.parse()`. Quotes are wrapped in custom CSS.
  - **IMAGE / VIDEO**: Rendered natively in the browser via `<img src="url">` and `<video controls>`. URLs are dynamically fetched from `block.url` or `block.s3Key` to support local uploads.

## Custom Design System (`app.css`)
The application defines a comprehensive design system using CSS Custom Properties (Variables) defined in `:root`.

```css
:root {
  /* Backgrounds */
  --bg-main: #ffffff;
  --bg-surface: #ffffff;
  
  /* Accent */
  --primary: #000000;
  
  /* Fonts */
  --font-sans: 'Special Elite', monospace;
  --font-mono: 'JetBrains Mono', monospace;
}
```
All styles intentionally utilize these variables to ensure UI consistency and ease of theme swapping (e.g., dark mode switching).
