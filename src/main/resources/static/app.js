// State Management
let state = {
  courses: [],
  categories: [],
  selectedCourseId: null,
  selectedCourseDetail: null,
  selectedCategoryId: null,
  trainerId: getOrCreateTrainerId(),
  defaultDomainId: 'd6b5e672-0000-4000-a000-000000000001', // Mock default domain UUID
  activeTab: 'authoring',
  evaluations: [], // DB evaluations history
  selectedSubmissionId: null,
  // Batch Roster state
  batches: [],
  selectedBatchId: null,
  batchLearners: [],
  batchFeedback: [],
  submissions: [
    {
      resultId: 'a38b1d92-2a3b-4c4d-a2f0-e67c8b1d9201',
      learnerId: 'e8b2a3b9-1d92-4f0e-a2f0-e8b2a3b91d01',
      learnerName: 'Alice Johnson',
      assessmentTitle: 'Spring Boot Fundamentals Test',
      aiScore: 78.50,
      submittedAt: '2026-07-06T14:32:00Z',
      answers: [
        {
          question: 'Explain the difference between @Component, @Service, and @Repository in Spring.',
          learnerAnswer: 'All three are stereotype annotations that scan and register beans in the application context. @Component is a general-purpose stereotype. @Service represents business logic, and @Repository represents persistence layers with automatic SQL Exception translation.',
          aiResult: 'Correct. Full marks (10/10).'
        },
        {
          question: 'Write a basic RestController with a GET mapping for /hello returning Hello World.',
          learnerAnswer: '@RestController\npublic class HelloController {\n    @GetMapping("/hello")\n    public String hello() { return "Hello World"; }\n}',
          aiResult: 'Correct. Code syntax is clean and matches requirements. (15/15)'
        },
        {
          question: 'Explain how Spring Boot handles Database Migrations using Flyway.',
          learnerAnswer: 'Flyway scans resources/db/migration for SQL scripts. When the application starts, it runs these scripts sequentially and records them in a flyway_schema_history table.',
          aiResult: 'Correct. Understood the schema history concept. (10/10)'
        }
      ]
    },
    {
      resultId: 'b38b1d92-2a3b-4c4d-a2f0-e67c8b1d9202',
      learnerId: 'e8b2a3b9-1d92-4f0e-a2f0-e8b2a3b91d02',
      learnerName: 'Bob Smith',
      assessmentTitle: 'Java JPA & Hibernate Quiz',
      aiScore: 55.00,
      submittedAt: '2026-07-06T16:10:00Z',
      answers: [
        {
          question: 'What is lazy loading and how is it configured in JPA?',
          learnerAnswer: 'Lazy loading loads related entities only when they are accessed. It is configured using FetchType.LAZY.',
          aiResult: 'Correct. (10/10)'
        },
        {
          question: 'What is the N+1 select problem and how do you resolve it?',
          learnerAnswer: 'It occurs when you load a list and JPA executes one select for the parent list plus N selects for the child references. It can be resolved by using fetch join in JPQL.',
          aiResult: 'Correct (15/15).'
        },
        {
          question: 'Explain entity lifecycle states in Hibernate.',
          learnerAnswer: 'States are transient, managed, detached, and removed.',
          aiResult: 'Incomplete explanation of transitions. Partial marks (5/10).'
        }
      ]
    },
    {
      resultId: 'c38b1d92-2a3b-4c4d-a2f0-e67c8b1d9203',
      learnerId: 'e8b2a3b9-1d92-4f0e-a2f0-e8b2a3b91d03',
      learnerName: 'Charlie Brown',
      assessmentTitle: 'LMS System Architecture Review',
      aiScore: 92.00,
      submittedAt: '2026-07-07T05:00:00Z',
      answers: [
        {
          question: 'Explain the single responsibility principle and how it applies to Controller-Service architecture.',
          learnerAnswer: 'Controllers only handle HTTP routing and requests. Services contain all business rules. Repositories handle database storage. This keeps classes focused on single tasks.',
          aiResult: 'Excellent explanation. (20/20)'
        }
      ]
    }
  ]
};

// Toast Notifications Helper
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span>${message}</span>
    <button style="background:none;border:none;color:white;cursor:pointer;font-weight:bold" onclick="this.parentElement.remove()">&times;</button>
  `;
  container.appendChild(toast);
  
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Local Storage for Trainer ID persistence
// Default to the well-known mock trainer UUID used by BatchDataSeeder
function getOrCreateTrainerId() {
  let id = localStorage.getItem('lms_trainer_id');
  if (!id) {
    id = '00000000-0000-4000-a000-000000000001';
    localStorage.setItem('lms_trainer_id', id);
  }
  return id;
}

function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// DOM Elements & Setup
document.addEventListener('DOMContentLoaded', () => {
  // Set initial trainer ID input
  const trainerInput = document.getElementById('trainer-id-input');
  if (trainerInput) {
    trainerInput.value = state.trainerId;
    trainerInput.addEventListener('change', (e) => {
      const val = e.target.value.trim();
      if (isValidUUID(val)) {
        state.trainerId = val;
        localStorage.setItem('lms_trainer_id', val);
        showToast('Trainer ID updated successfully', 'success');
        loadCourses();
      } else {
        showToast('Invalid UUID format for Trainer ID', 'error');
        e.target.value = state.trainerId;
      }
    });
  }

  // Bind forms and buttons
  document.getElementById('btn-new-category').addEventListener('click', openCreateCategoryModal);
  document.getElementById('btn-new-course').addEventListener('click', openCreateCourseModal);
  document.getElementById('form-category').addEventListener('submit', handleCreateCategory);
  document.getElementById('form-course').addEventListener('submit', handleCreateCourse);
  document.getElementById('form-edit-course').addEventListener('submit', handleEditCourse);
  document.getElementById('form-module').addEventListener('submit', handleCreateModule);
  document.getElementById('form-submodule').addEventListener('submit', handleCreateSubmodule);
  document.getElementById('form-content').addEventListener('submit', handleCreateContent);
  document.getElementById('form-batch').addEventListener('submit', handleCreateBatch);
  document.getElementById('form-add-learner').addEventListener('submit', handleAddLearner);
  document.getElementById('form-add-feedback').addEventListener('submit', handleCreateFeedback);
  
  // Wire Content Type conditional inputs in add content form
  document.getElementById('content-type').addEventListener('change', handleContentTypeChange);
  document.getElementById('content-heading-level').addEventListener('change', handleHeadingLevelChange);
  
  // Wire S3 File uploader
  setupFileUploader();
  setupCategoryImageUploader();

  // Load Initial Data
  loadCategories();
  loadCourses();
});

function isValidUUID(uuid) {
  const regex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
  return regex.test(uuid);
}

function getCategoryById(categoryId) {
  return state.categories.find(category => category.categoryId === categoryId) || null;
}

function getVisibleCourses() {
  if (!state.selectedCategoryId) {
    return state.courses;
  }

  return state.courses.filter(course => course.categoryId === state.selectedCategoryId);
}

function sanitizeCssColor(color) {
  if (!color) return '';

  const value = color.trim();
  if (/^#[0-9a-f]{3,8}$/i.test(value) || /^rgba?\([0-9\s.,%]+\)$/i.test(value) || /^hsla?\([0-9\s.,%]+\)$/i.test(value)) {
    return value;
  }

  return '';
}

function renderCategorySelectOptions(selectedValue = '') {
  if (state.categories.length === 0) {
    return '<option value="" disabled selected>Create a category first</option>';
  }

  return state.categories.map(category => {
    const isSelected = category.categoryId === selectedValue;
    return `<option value="${category.categoryId}" ${isSelected ? 'selected' : ''}>${escapeHTML(category.name)}</option>`;
  }).join('');
}

function populateCategorySelects() {
  const courseSelect = document.getElementById('course-category');
  const editSelect = document.getElementById('edit-course-category');

  if (courseSelect) {
    courseSelect.innerHTML = renderCategorySelectOptions(state.selectedCategoryId || courseSelect.value);
    if (state.selectedCategoryId && state.categories.some(category => category.categoryId === state.selectedCategoryId)) {
      courseSelect.value = state.selectedCategoryId;
    }
  }

  if (editSelect) {
    const currentDetailCategoryId = state.selectedCourseDetail?.categoryId || state.selectedCategoryId || '';
    editSelect.innerHTML = renderCategorySelectOptions(currentDetailCategoryId);
    if (currentDetailCategoryId) {
      editSelect.value = currentDetailCategoryId;
    }
  }
}

function syncSelectedCourseWithCategory() {
  const visibleCourses = getVisibleCourses();

  if (visibleCourses.length === 0) {
    state.selectedCourseId = null;
    state.selectedCourseDetail = null;
    renderCourseWorkspace();
    return;
  }

  const activeCourse = visibleCourses.find(course => course.courseId === state.selectedCourseId);
  if (!activeCourse) {
    selectCourse(visibleCourses[0].courseId);
    return;
  }
}

function openCreateCategoryModal() {
  const form = document.getElementById('form-category');
  if (form) {
    form.reset();
    const colorInput = document.getElementById('category-color');
    if (colorInput) {
      colorInput.value = '#0f766e';
    }
  }

  openModal('modal-category');
}

function openCreateCourseModal() {
  populateCategorySelects();

  const categorySelect = document.getElementById('course-category');
  if (categorySelect && state.selectedCategoryId) {
    categorySelect.value = state.selectedCategoryId;
  }

  openModal('modal-course');
}

function selectCategory(categoryId) {
  state.selectedCategoryId = categoryId;
  state.selectedCourseId = null;
  state.selectedCourseDetail = null;

  renderCategoriesList();
  renderCoursesList();
  populateCategorySelects();
  syncSelectedCourseWithCategory();
}

// Modal Management
function openModal(id) {
  document.getElementById(id).classList.add('active');
}

function closeModal(id) {
  document.getElementById(id).classList.remove('active');
  // Reset form inside if exists
  const form = document.querySelector(`#${id} form`);
  if (form) {
    form.reset();
    // Reset specific elements
    handleContentTypeChange({ target: { value: 'TEXT' } });
    resetFileUpload();
    if (id === 'modal-category') {
      resetCategoryImageUpload();
    }
  }
}

// API Requests Helpers
async function fetchAPI(url, options = {}) {
  const defaultHeaders = {
    'Content-Type': 'application/json',
    'X-Trainer-Id': state.trainerId
  };
  
  options.headers = {
    ...defaultHeaders,
    ...options.headers
  };
  
  const response = await fetch(url, options);
  if (!response.ok) {
    let errMsg = `Request failed: ${response.status} ${response.statusText}`;
    try {
      const errorJson = await response.json();
      if (errorJson.message) errMsg = errorJson.message;
    } catch(e) {}
    throw new Error(errMsg);
  }
  
  if (response.status === 204) return null;
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

// Fetch lists of courses
async function loadCategories() {
  try {
    const list = await fetchAPI('/api/v1/trainer/categories');
    state.categories = list;

    if (state.selectedCategoryId && !list.some(category => category.categoryId === state.selectedCategoryId)) {
      state.selectedCategoryId = list.length > 0 ? list[0].categoryId : null;
    }

    if (!state.selectedCategoryId && list.length > 0) {
      state.selectedCategoryId = list[0].categoryId;
    }

    renderCategoriesList();
    populateCategorySelects();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function loadCourses() {
  try {
    const list = await fetchAPI('/api/v1/trainer/courses');
    state.courses = list;
    renderCoursesList();
    populateCategorySelects();
    syncSelectedCourseWithCategory();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// Load Course Detailed Tree
async function loadCourseDetail(courseId) {
  try {
    const detail = await fetchAPI(`/api/v1/trainer/courses/${courseId}`);
    state.selectedCourseDetail = detail;
    state.selectedCategoryId = detail.categoryId;
    renderCategoriesList();
    populateCategorySelects();
    renderCourseWorkspace();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function selectCourse(courseId) {
  state.selectedCourseId = courseId;
  renderCoursesList();
  loadCourseDetail(courseId);
}

// Handlers for Form Submissions

async function handleCreateCourse(e) {
  e.preventDefault();
  const title = document.getElementById('course-title').value.trim();
  const summary = document.getElementById('course-summary-input').value.trim();
  const level = document.getElementById('course-level').value;
  const categoryId = document.getElementById('course-category').value;

  if (!categoryId) {
    showToast('Select a category before creating a course', 'error');
    return;
  }
  
  try {
    const newCourse = await fetchAPI('/api/v1/trainer/courses', {
      method: 'POST',
      body: JSON.stringify({
        categoryId,
        title,
        summary,
        level
      })
    });
    
    showToast('Course created successfully');
    closeModal('modal-course');
    await loadCourses();
    selectCourse(newCourse.courseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function handleCreateCategory(e) {
  e.preventDefault();

  const name = document.getElementById('category-name').value.trim();
  const icon = document.getElementById('category-icon').value.trim();
  const color = document.getElementById('category-color').value.trim();
  const description = document.getElementById('category-description').value.trim();

  if (!name) {
    showToast('Category name is required', 'error');
    return;
  }

  try {
    const category = await fetchAPI('/api/v1/trainer/categories', {
      method: 'POST',
      body: JSON.stringify({
        name,
        icon,
        color,
        description
      })
    });

    showToast('Category created successfully');
    closeModal('modal-category');
    await loadCategories();
    selectCategory(category.categoryId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function handleCreateModule(e) {
  e.preventDefault();
  const title = document.getElementById('module-title-input').value.trim();
  const sortOrder = parseInt(document.getElementById('module-sort').value) || 1;
  
  try {
    await fetchAPI(`/api/v1/trainer/courses/${state.selectedCourseId}/modules`, {
      method: 'POST',
      body: JSON.stringify({
        title,
        sortOrder
      })
    });
    
    showToast('Module added successfully');
    closeModal('modal-module');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function handleCreateSubmodule(e) {
  e.preventDefault();
  const moduleId = document.getElementById('target-module-id').value;
  const title = document.getElementById('submodule-title-input').value.trim();
  const sortOrder = parseInt(document.getElementById('submodule-sort').value) || 1;
  const estMinutes = parseInt(document.getElementById('submodule-est').value) || null;
  
  try {
    await fetchAPI(`/api/v1/trainer/modules/${moduleId}/submodules`, {
      method: 'POST',
      body: JSON.stringify({
        title,
        sortOrder,
        estMinutes
      })
    });
    
    showToast('Lesson (Submodule) added successfully');
    closeModal('modal-submodule');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function handleCreateContent(e) {
  e.preventDefault();
  const submoduleId = document.getElementById('target-submodule-id').value;
  const contentId = document.getElementById('target-content-id').value;
  const type = document.getElementById('content-type').value;
  const body = document.getElementById('content-body').value.trim();
  const language = document.getElementById('content-language').value;
  const sortOrder = parseInt(document.getElementById('content-sort').value) || 1;
  const headingLevel = document.getElementById('content-heading-level') ? document.getElementById('content-heading-level').value : '';
  const headingText = document.getElementById('content-heading-text') ? document.getElementById('content-heading-text').value.trim() : '';
  
  // Resolve S3 key if media

  let s3Key = null;

  if (['IMAGE', 'PDF', 'VIDEO'].includes(type)) {
    // Read upload key (either S3 uploader resolved it, or the manual fallback input did)
    s3Key = document.getElementById('resolved-s3-key').value.trim();
    if (!s3Key) {
      showToast('A file must be uploaded or a manual S3 key provided for media content blocks', 'error');
      return;
    }
  }
  
  try {
    const payload = {
        type,
        body: ['TEXT', 'CODE', 'QUOTE', 'HEADING'].includes(type) ? body : null,
        s3Key,
        language: type === 'CODE' ? language : null,
        headingLevel: headingLevel || null,
        headingText: headingText || null,
        sortOrder
    };
    
    if (contentId) {
      await fetchAPI(`/api/v1/trainer/content/${contentId}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      });
      showToast('Content block updated successfully');
    } else {
      await fetchAPI(`/api/v1/trainer/submodules/${submoduleId}/content`, {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      showToast('Content block added successfully');
    }
    
    closeModal('modal-content');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function publishActiveCourse() {
  if (!state.selectedCourseId) return;
  if (!confirm('Are you sure you want to publish this course? The structure will be frozen.')) return;
  
  try {
    const published = await fetchAPI(`/api/v1/trainer/courses/${state.selectedCourseId}/publish`, {
      method: 'POST'
    });
    showToast('Course published successfully! Structure frozen.');
    await loadCourses();
    selectCourse(published.courseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// Rendering Logic

function renderCategoriesList() {
  const container = document.getElementById('categories-list');
  if (!container) return;

  container.innerHTML = '';

  if (state.categories.length === 0) {
    container.innerHTML = `
      <div class="empty-stack">
        <div class="empty-stack-icon">🏷️</div>
        <div class="empty-stack-title">No categories yet</div>
        <div class="empty-stack-copy">Create the first category to start building the hierarchy.</div>
      </div>
    `;
    return;
  }

  state.categories.forEach(category => {
    const isActive = category.categoryId === state.selectedCategoryId;
    const courseCount = state.courses.filter(course => course.categoryId === category.categoryId).length;
    const item = document.createElement('button');
    item.type = 'button';
    item.className = `category-item ${isActive ? 'active' : ''}`;
    item.onclick = () => selectCategory(category.categoryId);

    const color = sanitizeCssColor(category.color);
    const colorStyle = color ? `style="background:${color};"` : '';

    // Prevent the click on the delete button from selecting the category
    const isImage = category.icon && (category.icon.includes('/') || category.icon.includes('.') || category.icon.length > 8);
    const iconContent = isImage 
      ? `<img src="${escapeHTML(category.icon)}" alt="">`
      : escapeHTML(category.icon || '📚');

    item.innerHTML = `
      <div class="category-item-top">
        <div class="category-icon" ${colorStyle}>${iconContent}</div>
        <div class="category-item-copy">
          <div class="category-item-title">${escapeHTML(category.name)}</div>
          <div class="category-item-meta">${courseCount} course${courseCount === 1 ? '' : 's'}</div>
        </div>
      </div>
      <div class="category-item-footer">
        <span class="badge ${category.active ? 'badge-published' : 'badge-draft'}">${category.active ? 'ACTIVE' : 'INACTIVE'}</span>
        ${category.description ? `<span class="category-item-note">${escapeHTML(category.description)}</span>` : ''}
        <button
          type="button"
          class="btn btn-secondary btn-sm"
          style="margin-left:auto; color:#ef4444; border-color: rgba(239, 68, 68, 0.2);"
          onclick="event.stopPropagation(); deleteCategory('${category.categoryId}', ${courseCount})"
          title="Delete category (only allowed when it has no courses)"
        >
          Delete
        </button>
      </div>
    `;

    container.appendChild(item);
  });
}

function renderCoursesList() {
  const container = document.getElementById('courses-list');
  container.innerHTML = '';

  const visibleCourses = getVisibleCourses();

  if (state.courses.length === 0) {
    container.innerHTML = '<div class="empty-stack"><div class="empty-stack-icon">📘</div><div class="empty-stack-title">No courses yet</div><div class="empty-stack-copy">Create a category first, then add the course you want to test.</div></div>';
    return;
  }

  if (visibleCourses.length === 0) {
    const category = getCategoryById(state.selectedCategoryId);
    container.innerHTML = `<div class="empty-stack"><div class="empty-stack-icon">🧭</div><div class="empty-stack-title">No courses in ${escapeHTML(category?.name || 'this category')}</div><div class="empty-stack-copy">Use the New Course button to create one in the selected category.</div></div>`;
    return;
  }
  
  visibleCourses.forEach(course => {
    const isActive = course.courseId === state.selectedCourseId;
    const item = document.createElement('div');
    item.className = `course-item ${isActive ? 'active' : ''}`;
    item.onclick = () => selectCourse(course.courseId);
    
    item.innerHTML = `
      <div class="course-item-eyebrow">${escapeHTML(course.categoryName || 'Uncategorized')}</div>
      <div class="course-item-title">${escapeHTML(course.title)}</div>
      ${course.summary ? `<div class="course-item-summary">${escapeHTML(course.summary)}</div>` : ''}
      <div class="course-item-meta">
        <span class="badge badge-level">${escapeHTML(course.level)}</span>
        <span class="badge badge-${course.status.toLowerCase()}">${escapeHTML(course.status)}</span>
      </div>
    `;
    
    container.appendChild(item);
  });
}

function renderCourseWorkspace() {
  const container = document.getElementById('workspace-content');
  container.innerHTML = '';
  
  const detail = state.selectedCourseDetail;
  if (!detail) {
    container.innerHTML = `
      <div class="course-detail">
        <div class="hierarchy-lab hierarchy-lab-empty">
          <div>
            <div class="hierarchy-eyebrow">Hierarchy Lab</div>
            <h2>Category → Course → Module → Lesson</h2>
            <p>Create a category, attach a course, and then build out the nested module tree to test the authoring APIs end to end.</p>
          </div>
          <div class="hierarchy-actions">
            <button class="btn btn-secondary" onclick="openCreateCategoryModal()">+ New Category</button>
            <button class="btn btn-primary" onclick="openCreateCourseModal()">+ New Course</button>
          </div>
        </div>
        <div class="placeholder-view placeholder-card">
          <div class="placeholder-icon">🗂️</div>
          <h3>No Course Selected</h3>
          <p>Pick a course from the sidebar or create a new one under the selected category.</p>
        </div>
      </div>
    `;
    return;
  }
  
  const selectedCategory = getCategoryById(detail.categoryId);
  const moduleCount = detail.modules ? detail.modules.length : 0;
  const lessonCount = detail.modules ? detail.modules.reduce((count, module) => count + (module.submodules ? module.submodules.length : 0), 0) : 0;
  const blockCount = detail.modules ? detail.modules.reduce((count, module) => count + (module.submodules ? module.submodules.reduce((nestedCount, submodule) => nestedCount + (submodule.contentBlocks ? submodule.contentBlocks.length : 0), 0) : 0), 0) : 0;
  const isPublished = detail.status === 'PUBLISHED';
  
  // Render Course header & modules
  let html = `
    <div class="course-detail">
      <div class="hierarchy-lab">
        <div>
          <div class="hierarchy-eyebrow">Hierarchy Lab</div>
          <h2>${escapeHTML(detail.title)}</h2>
          <p>${escapeHTML(selectedCategory?.name || detail.categoryName || 'Uncategorized')} · ${moduleCount} section${moduleCount === 1 ? '' : 's'}, ${lessonCount} lesson${lessonCount === 1 ? '' : 's'}, ${blockCount} block${blockCount === 1 ? '' : 's'}</p>
        </div>
        <div class="hierarchy-actions">
          <button class="btn btn-secondary" onclick="openCreateCategoryModal()">+ New Category</button>
          <button class="btn btn-primary" onclick="openCreateCourseModal()">+ New Course</button>
          <button class="btn btn-secondary" onclick="openAddModuleModal()">+ Add Section</button>
        </div>
      </div>

      <div class="course-meta-card">
        <div class="course-meta-header">
          <div class="course-title-section">
            <span class="badge badge-level" style="margin-bottom:8px;display:inline-block">Level: ${escapeHTML(detail.level)}</span>
            <h2>${escapeHTML(detail.title)}</h2>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <a href="viewer.html?courseId=${detail.courseId}" target="_blank" class="btn btn-secondary" style="text-decoration: none; color: inherit; display: inline-flex; align-items: center; justify-content: center;">👁️ Preview as Viewer</a>
            <button class="btn btn-secondary" onclick="openEditCourseModal()">Edit Info</button>
            <button class="btn btn-danger" onclick="deleteActiveCourse()" style="background-color:#ef4444;border-color:#ef4444;color:white">Delete</button>
            ${isPublished 
              ? `<span class="badge badge-published" style="padding: 8px 16px; font-size: 0.9rem">PUBLISHED (v${detail.version})</span>`
              : `<button class="btn btn-primary" onclick="publishActiveCourse()">Publish Course</button>`
            }
          </div>
        </div>
        <p class="course-summary">${escapeHTML(detail.summary || 'No summary provided.')}</p>
        
        <div class="course-info-grid">
          <div class="info-item">
            <span class="info-label">Category</span>
            <span class="info-value">${escapeHTML(detail.categoryName)}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Course ID</span>
            <span class="info-value" style="font-family:var(--font-mono);font-size:0.8rem">${detail.courseId}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Created At</span>
            <span class="info-value">${new Date(detail.createdAt).toLocaleString()}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Status</span>
            <span class="info-value">
              <span class="badge badge-${detail.status.toLowerCase()}">${detail.status}</span>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">Hierarchy</span>
            <span class="info-value">${moduleCount} modules · ${lessonCount} lessons · ${blockCount} blocks</span>
          </div>
        </div>
      </div>
      
      <div class="section-header">
        <h3>Course Sections (Modules)</h3>
        <button class="btn btn-secondary btn-sm" onclick="openAddModuleModal()">+ Add Section</button>
      </div>
      
      <div class="modules-container">
  `;
  
  if (!detail.modules || detail.modules.length === 0) {
    html += `
        <div style="color:var(--text-muted);padding:40px;text-align:center;background:rgba(255,255,255,0.01);border:1px dashed var(--border-color);border-radius:var(--radius-lg)">
          This course has no modules yet. Click "+ Add Section" to build the first one.
        </div>
    `;
  } else {
    detail.modules.forEach((mod, modIdx) => {
      html += `
        <div class="module-card">
          <div class="module-header">
            <div class="module-title-wrapper">
              <div class="module-index">${modIdx + 1}</div>
              <div class="module-title">${escapeHTML(mod.title)}</div>
            </div>
            <div style="display:flex;gap:6px;align-items:center">
              <button class="btn btn-secondary btn-sm" onclick="openAddSubmoduleModal('${mod.moduleId}')">+ Add Lesson</button>
              <button class="btn btn-secondary btn-sm" onclick="openEditModuleModal('${mod.moduleId}', '${escapeHTML(mod.title).replace(/'/g, "\\'")}')">✏️</button>
              <button class="btn btn-secondary btn-sm" onclick="deleteModule('${mod.moduleId}')" style="color:#ef4444">Delete</button>
            </div>
          </div>
          <div class="submodules-list">
      `;
      
      if (!mod.submodules || mod.submodules.length === 0) {
        html += `
            <div style="color:var(--text-muted);font-size:0.85rem;text-align:center;padding:20px">No lessons added to this section yet.</div>
        `;
      } else {
        mod.submodules.forEach(sub => {
          html += `
            <div class="submodule-card">
              <div class="submodule-header">
                <div class="submodule-title">📖 ${escapeHTML(sub.title)}</div>
                <div style="display:flex;align-items:center;gap:8px">
                  ${sub.estMinutes ? `<span class="submodule-est">⏱️ ${sub.estMinutes} mins</span>` : ''}
                  <button class="btn btn-secondary btn-sm" style="padding:4px 8px;font-size:0.75rem" onclick="openAddContentModal('${sub.submoduleId}')">+ Add Block</button>
                  <button class="btn btn-secondary btn-sm" style="padding:4px 8px;font-size:0.75rem" onclick="openEditSubmoduleModal('${sub.submoduleId}', '${escapeHTML(sub.title).replace(/'/g, "\\'")}')">✏️</button>
                  <button class="btn btn-secondary btn-sm" style="padding:4px 8px;font-size:0.75rem;color:#ef4444" onclick="deleteSubmodule('${sub.submoduleId}')">Delete</button>
                </div>
              </div>
              <div class="content-blocks-list">
          `;
          
          if (!sub.contentBlocks || sub.contentBlocks.length === 0) {
            html += `
                <div style="color:var(--text-muted);font-size:0.8rem;text-align:center;padding:10px">No content blocks.</div>
            `;
          } else {
            sub.contentBlocks.forEach((content, idx) => {
              html += `
                <div class="content-block">
                  <div class="content-block-header">
                    <span class="content-block-type">${content.type}</span>
                    <div style="display:flex;align-items:center;gap:8px">
                      <div style="display:flex;align-items:center;gap:4px">
                        ${idx > 0 ? `<button class="btn btn-secondary btn-sm" style="padding:2px 6px;font-size:0.7rem" onclick="moveContentUp('${sub.submoduleId}', ${idx})" title="Move Up">↑</button>` : ''}
                        ${idx < sub.contentBlocks.length - 1 ? `<button class="btn btn-secondary btn-sm" style="padding:2px 6px;font-size:0.7rem" onclick="moveContentDown('${sub.submoduleId}', ${idx})" title="Move Down">↓</button>` : ''}
                        <button class="btn btn-secondary btn-sm" style="padding:2px 6px;font-size:0.7rem" onclick="openEditContentModal('${sub.submoduleId}', '${content.contentId}')" title="Edit">✏️</button>
                        <button class="btn btn-secondary btn-sm" style="padding:2px 6px;font-size:0.7rem;color:#ef4444;margin-left:4px" onclick="deleteContent('${content.contentId}')" title="Delete">Delete</button>
                      </div>
                    </div>
                  </div>
              `;

              // Heading rendering (supports backend field headingLevel)
              const headingLevel = content.headingLevel || '';
              const headingTag = headingLevel ? headingLevel : null;
              const headingHtml = headingTag ? `<div class="content-block-heading" style="margin-top:10px"><span class="badge badge-level" style="margin-bottom:6px;display:inline-block">Heading: ${escapeHTML(headingLevel)}</span></div>` : '';
              
              html += headingHtml;
              
              if (content.type === 'TEXT') {
                html += `<div class="content-block-body">${escapeHTML(content.body)}</div>`;
              } else if (content.type === 'CODE') {
                html += `
                  <div class="content-block-body">${escapeHTML(content.body || '')}</div>
                  ${content.language ? `<span class="badge badge-level" style="margin-top:8px;display:inline-block">${content.language}</span>` : ''}
                `;
              } else {
                // Media type
                html += `
                  <div class="content-block-media">
                    <span>📦 S3 Key:</span>
                    <span>${escapeHTML(content.s3Key)}</span>
                  </div>
                `;
              }
              
              html += `</div>`; // Close content-block
            });
          }
          
          html += `
              </div>
            </div> <!-- Close submodule-card -->
          `;
        });
      }
      
      html += `
          </div>
        </div> <!-- Close module-card -->
      `;
    });
  }
  
  html += `
      </div>
    </div> <!-- Close course-detail -->
  `;
  
  container.innerHTML = html;
}

// Modal open helpers that set targeted IDs
function openAddModuleModal() {
  openModal('modal-module');
}

function openAddSubmoduleModal(moduleId) {
  document.getElementById('target-module-id').value = moduleId;
  openModal('modal-submodule');
}

function openAddContentModal(submoduleId) {
  document.getElementById('form-content').reset();
  document.getElementById('target-submodule-id').value = submoduleId;
  document.getElementById('target-content-id').value = '';
  document.getElementById('content-modal-title').textContent = 'Add Content Block';
  document.getElementById('content-submit-btn').textContent = 'Add Block';
  handleContentTypeChange({target: {value: 'HEADING'}});
  openModal('modal-content');
}

function openEditContentModal(submoduleId, contentId) {
  const detail = state.selectedCourseDetail;
  let sub = null;
  for(let m of detail.modules) {
     sub = m.submodules.find(s => s.submoduleId === submoduleId);
     if(sub) break;
  }
  if (!sub) return;
  const content = sub.contentBlocks.find(c => c.contentId === contentId);
  if (!content) return;
  
  document.getElementById('form-content').reset();
  document.getElementById('target-submodule-id').value = submoduleId;
  document.getElementById('target-content-id').value = contentId;
  document.getElementById('content-type').value = content.type;
  document.getElementById('content-sort').value = content.sortOrder || 1;
  document.getElementById('content-heading-level').value = content.headingLevel || '';
  document.getElementById('content-heading-text').value = content.headingText || '';
  document.getElementById('content-body').value = content.body || '';
  document.getElementById('content-language').value = content.language || '';
  document.getElementById('resolved-s3-key').value = content.s3Key || '';
  
  document.getElementById('content-modal-title').textContent = 'Edit Content Block';
  document.getElementById('content-submit-btn').textContent = 'Update Block';
  handleContentTypeChange({target: {value: content.type}});
  handleHeadingLevelChange({target: {value: content.headingLevel || ''}});
  
  openModal('modal-content');
}

// Content Type Conditional Rendering
function handleContentTypeChange(e) {
  const type = e.target.value;
  const textGroup = document.getElementById('group-body');
  const codeGroup = document.getElementById('group-language');
  const mediaGroup = document.getElementById('group-media');
  
  if (['TEXT', 'QUOTE', 'HEADING'].includes(type)) {
    textGroup.style.display = 'block';
    codeGroup.style.display = 'none';
    mediaGroup.style.display = 'none';
  } else if (type === 'CODE') {
    textGroup.style.display = 'block';
    codeGroup.style.display = 'block';
    mediaGroup.style.display = 'none';
  } else {
    // IMAGE, PDF, VIDEO
    textGroup.style.display = 'none';
    codeGroup.style.display = 'none';
    mediaGroup.style.display = 'block';
  }
}

// Heading Level Conditional Styling
function handleHeadingLevelChange(e) {
  const level = e.target.value;
  const headingInput = document.getElementById('content-heading-text');
  
  headingInput.className = 'form-control'; // reset class
  if (level) {
    headingInput.classList.add(`format-${level.toLowerCase()}`);
  }
}

// S3 Media Uploading Client
function setupFileUploader() {
  const fileInput = document.getElementById('media-file');
  const uploadWrapper = document.getElementById('media-upload-wrapper');
  const progressBar = document.getElementById('media-progress');
  const progressFill = document.getElementById('media-progress-fill');
  const fileInfo = document.getElementById('media-info');
  const fallbackBox = document.getElementById('media-fallback');
  const resolvedKeyInput = document.getElementById('resolved-s3-key');
  
  uploadWrapper.addEventListener('click', () => fileInput.click());
  
  fileInput.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    // UI Update
    fileInfo.textContent = `Selected: ${file.name} (${formatBytes(file.size)})`;
    progressBar.style.display = 'block';
    progressFill.style.width = '0%';
    fallbackBox.style.display = 'none';
    resolvedKeyInput.value = '';
    
    try {
      // Create form data and upload to local folder directly
      const formData = new FormData();
      formData.append('file', file);
      
      const xhr = new XMLHttpRequest();
      xhr.open('POST', '/api/v1/trainer/upload/local', true);
      xhr.setRequestHeader('X-Trainer-Id', state.trainerId);
      
      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          const percent = Math.round((event.loaded / event.total) * 100);
          progressFill.style.width = `${percent}%`;
        }
      };
      
      xhr.onload = () => {
        if (xhr.status === 200 || xhr.status === 201) {
          try {
            const data = JSON.parse(xhr.responseText);
            showToast('File uploaded successfully');
            fileInfo.innerHTML = `✅ Uploaded!<br><span style="font-size:0.75rem;color:var(--text-muted)">Path: ${data.url}</span>`;
            resolvedKeyInput.value = data.url;
            if (document.getElementById('manual-s3-key')) {
              document.getElementById('manual-s3-key').value = data.url;
            }
          } catch (e) {
            showToast('Failed to parse upload response', 'error');
          }
        } else {
          showToast(`Upload failed: ${xhr.status} ${xhr.statusText}`, 'error');
        }
      };
      
      xhr.onerror = () => {
        showToast('Upload network connection failed.', 'error');
      };
      
      xhr.send(formData);
      
    } catch (error) {
      console.error(error);
      showToast('Upload failed: ' + error.message, 'error');
      progressBar.style.display = 'none';
      fileInfo.innerHTML = `❌ Upload error: ${error.message}`;
    }
  });
  
  // Wire manual key input changes
  document.getElementById('manual-s3-key').addEventListener('input', (e) => {
    resolvedKeyInput.value = e.target.value.trim();
  });
}

function resetFileUpload() {
  document.getElementById('media-file').value = '';
  document.getElementById('media-info').textContent = 'Drop file here or click to browse';
  document.getElementById('media-progress').style.display = 'none';
  document.getElementById('media-progress-fill').style.width = '0%';
  document.getElementById('media-fallback').style.display = 'none';
  document.getElementById('resolved-s3-key').value = '';
  document.getElementById('manual-s3-key').value = '';
}

function formatBytes(bytes, decimals = 2) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

// Utility to escape HTML and prevent XSS
function escapeHTML(str) {
  if (!str) return '';
  return str.replace(/[&<>'"]/g, 
    tag => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      "'": '&#39;',
      '"': '&quot;'
    }[tag] || tag)
  );
}

// Tab Switching
window.switchTab = function(tab) {
  state.activeTab = tab;
  
  const menuAuthoring = document.getElementById('menu-authoring');
  const menuEvaluations = document.getElementById('menu-evaluations');
  const menuBatches = document.getElementById('menu-batches');
  const viewAuthoring = document.getElementById('view-authoring');
  const viewEvaluations = document.getElementById('view-evaluations');
  const viewBatches = document.getElementById('view-batches');
  
  // Deactivate all
  menuAuthoring.classList.remove('active');
  menuEvaluations.classList.remove('active');
  menuBatches.classList.remove('active');
  viewAuthoring.style.display = 'none';
  viewEvaluations.style.display = 'none';
  viewBatches.style.display = 'none';
  
  if (tab === 'authoring') {
    menuAuthoring.classList.add('active');
    viewAuthoring.style.display = 'flex';
    loadCourses();
  } else if (tab === 'evaluations') {
    menuEvaluations.classList.add('active');
    viewEvaluations.style.display = 'flex';
    loadEvaluations();
    renderRoster();
  } else if (tab === 'batches') {
    menuBatches.classList.add('active');
    viewBatches.style.display = 'flex';
    loadBatches();
  }
};

// Evaluations history and submissions loaders
async function loadEvaluations() {
  try {
    const list = await fetchAPI('/api/v1/trainer/evaluations');
    state.evaluations = list;
    renderSubmissionWorkspace();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function renderRoster() {
  const container = document.getElementById('roster-list');
  container.innerHTML = '';
  
  state.submissions.forEach(sub => {
    const isActive = sub.resultId === state.selectedSubmissionId;
    const item = document.createElement('div');
    item.className = `roster-item ${isActive ? 'active' : ''}`;
    item.onclick = () => selectSubmission(sub.resultId);
    
    // Check if evaluated
    const evaluation = state.evaluations.find(ev => ev.resultId === sub.resultId);
    const scoreText = evaluation 
      ? `Override: ${evaluation.overrideScore.toFixed(2)}`
      : `AI: ${sub.aiScore.toFixed(2)}`;
    
    item.innerHTML = `
      <div class="roster-name">${escapeHTML(sub.learnerName)}</div>
      <div class="roster-meta">
        <span>${escapeHTML(sub.assessmentTitle)}</span>
        <span class="badge ${evaluation ? 'badge-published' : 'badge-draft'}">${scoreText}</span>
      </div>
    `;
    container.appendChild(item);
  });
}

function selectSubmission(resultId) {
  state.selectedSubmissionId = resultId;
  renderRoster();
  renderSubmissionWorkspace();
}

function renderSubmissionWorkspace() {
  const container = document.getElementById('evaluations-workspace');
  container.innerHTML = '';
  
  const sub = state.submissions.find(s => s.resultId === state.selectedSubmissionId);
  if (!sub) {
    container.innerHTML = `
      <div class="placeholder-view">
        <div class="placeholder-icon">📝</div>
        <h3>No Submission Selected</h3>
        <p>Choose a student submission from the roster sidebar to review and override scores.</p>
      </div>
    `;
    return;
  }
  
  // Find database evaluation if already submitted
  const evaluation = state.evaluations.find(ev => ev.resultId === sub.resultId);
  
  let qasHtml = '';
  sub.answers.forEach((ans, idx) => {
    qasHtml += `
      <div class="qa-item">
        <div class="qa-question">Q${idx + 1}: ${escapeHTML(ans.question)}</div>
        <div class="qa-answer">${escapeHTML(ans.learnerAnswer)}</div>
        <div class="qa-result">
          <span style="color:var(--primary-hover);font-weight:600">🤖 AI Feedback:</span>
          <span style="color:var(--text-muted)">${escapeHTML(ans.aiResult)}</span>
        </div>
      </div>
    `;
  });
  
  const html = `
    <div class="eval-card">
      <div class="course-meta-header">
        <div>
          <span class="badge badge-level" style="margin-bottom:8px;display:inline-block">Assessment Submission</span>
          <h2>${escapeHTML(sub.learnerName)}</h2>
          <div style="color:var(--text-muted);font-size:0.9rem;margin-top:4px">
            Test: <strong>${escapeHTML(sub.assessmentTitle)}</strong> | Submitted At: ${new Date(sub.submittedAt).toLocaleString()}
          </div>
        </div>
        <div style="text-align:right">
          <span class="info-label" style="display:block;margin-bottom:4px">Original AI Score</span>
          <span style="font-size:1.8rem;font-weight:700;color:var(--warning)">${sub.aiScore.toFixed(2)}</span>
        </div>
      </div>
      
      <h3 style="margin-top:24px;border-bottom:1px solid var(--border-color);padding-bottom:8px">Answer Sheet</h3>
      <div class="qa-list">${qasHtml}</div>
      
      <h3 style="border-bottom:1px solid var(--border-color);padding-bottom:8px;margin-bottom:16px">Trainer Score Override</h3>
      
      <form id="form-evaluation" onsubmit="submitScoreOverride(event)">
        <input type="hidden" id="eval-result-id" value="${sub.resultId}">
        <input type="hidden" id="eval-learner-id" value="${sub.learnerId}">
        
        <div class="form-group">
          <label for="eval-override-score">Override Score (0.00 - 100.00)</label>
          <input type="number" id="eval-override-score" class="form-control" step="0.01" min="0" max="100" 
                 value="${evaluation ? evaluation.overrideScore : sub.aiScore}" required style="width:200px">
        </div>
        
        <div class="form-group">
          <label for="eval-comments">Evaluation Review Comments</label>
          <textarea id="eval-comments" class="form-control" rows="3" placeholder="Provide grading feedback for the student...">${evaluation ? escapeHTML(evaluation.comments) : ''}</textarea>
        </div>
        
        <button type="submit" class="btn btn-primary">Save Evaluation Override</button>
      </form>
    </div>
    
    <div class="history-section">
      <h3>Evaluation Override History (Trainer DB Logs)</h3>
      <div id="evaluations-history-list" style="margin-top:16px">
        <!-- History rendered below -->
      </div>
    </div>
  `;
  
  container.innerHTML = html;
  renderEvaluationsHistory();
}

window.submitScoreOverride = async function(e) {
  e.preventDefault();
  const resultId = document.getElementById('eval-result-id').value;
  const learnerId = document.getElementById('eval-learner-id').value;
  const overrideScore = parseFloat(document.getElementById('eval-override-score').value);
  const comments = document.getElementById('eval-comments').value.trim();
  
  try {
    await fetchAPI('/api/v1/trainer/evaluations', {
      method: 'POST',
      body: JSON.stringify({
        learnerId,
        resultId,
        overrideScore,
        comments
      })
    });
    
    showToast('Evaluation override saved to DB successfully');
    await loadEvaluations();
    renderRoster();
  } catch (error) {
    showToast(error.message, 'error');
  }
};

function renderEvaluationsHistory() {
  const container = document.getElementById('evaluations-history-list');
  if (!container) return;
  container.innerHTML = '';
  
  if (state.evaluations.length === 0) {
    container.innerHTML = '<div style="color:var(--text-muted);font-size:0.85rem">No evaluations recorded yet in database logs.</div>';
    return;
  }
  
  state.evaluations.forEach(ev => {
    // Find learner name
    const sub = state.submissions.find(s => s.learnerId === ev.learnerId);
    const name = sub ? sub.learnerName : 'Unknown Learner';
    const test = sub ? sub.assessmentTitle : 'Assessment';
    
    const card = document.createElement('div');
    card.className = 'history-card';
    card.innerHTML = `
      <div class="history-header">
        <strong>${escapeHTML(name)}</strong>
        <span>${new Date(ev.createdAt).toLocaleString()}</span>
      </div>
      <div style="font-size:0.85rem;margin-bottom:8px">
        Test: <em>${escapeHTML(test)}</em> | 
        Override Score: <strong style="color:var(--success)">${ev.overrideScore.toFixed(2)}</strong>
      </div>
      ${ev.comments ? `<div style="font-size:0.8rem;background:rgba(255,255,255,0.02);padding:8px;border-left:2px solid var(--primary)">${escapeHTML(ev.comments)}</div>` : ''}
    `;
    container.appendChild(card);
  });
}

// Edit & Delete Course Functions
window.openEditCourseModal = function() {
  const detail = state.selectedCourseDetail;
  if (!detail) return;
  
  populateCategorySelects();
  document.getElementById('edit-course-title').value = detail.title;
  document.getElementById('edit-course-summary-input').value = detail.summary || '';
  document.getElementById('edit-course-level').value = detail.level;
  document.getElementById('edit-course-category').value = detail.categoryId;
  
  openModal('modal-edit-course');
};

window.handleEditCourse = async function(e) {
  e.preventDefault();
  if (!state.selectedCourseId) return;
  
  const title = document.getElementById('edit-course-title').value.trim();
  const summary = document.getElementById('edit-course-summary-input').value.trim();
  const level = document.getElementById('edit-course-level').value;
  const categoryId = document.getElementById('edit-course-category').value;

  if (!categoryId) {
    showToast('Select a category before saving the course', 'error');
    return;
  }
  
  try {
    const updated = await fetchAPI(`/api/v1/trainer/courses/${state.selectedCourseId}`, {
      method: 'PUT',
      body: JSON.stringify({
        categoryId,
        title,
        summary,
        level
      })
    });
    
    showToast('Course metadata updated successfully');
    closeModal('modal-edit-course');
    await loadCourses();
    selectCourse(updated.courseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};

window.deleteActiveCourse = async function() {
  if (!state.selectedCourseId) return;
  if (!confirm('WARNING: Are you sure you want to delete this course? This will permanently delete all modules, lessons, and content attached to it.')) return;
  
  try {
    await fetchAPI(`/api/v1/trainer/courses/${state.selectedCourseId}`, {
      method: 'DELETE'
    });
    
    showToast('Course deleted successfully');
    state.selectedCourseId = null;
    state.selectedCourseDetail = null;
    await loadCourses();
    
    // If no courses remain, render placeholder
    if (state.courses.length === 0) {
      renderCourseWorkspace();
    }
  } catch (error) {
    showToast(error.message, 'error');
  }
};

// ── Module edit / delete ────────────────────────────────────────

window.openEditModuleModal = function(moduleId, currentTitle) {
  const newTitle = prompt('Edit section title:', currentTitle);
  if (newTitle === null || newTitle.trim() === '') return;
  
  fetchAPI(`/api/v1/trainer/modules/${moduleId}`, {
    method: 'PUT',
    body: JSON.stringify({ title: newTitle.trim(), sortOrder: 1 })
  }).then(() => {
    showToast('Section updated');
    loadCourseDetail(state.selectedCourseId);
  }).catch(err => showToast(err.message, 'error'));
};

window.deleteModule = async function(moduleId) {
  if (!confirm('Delete this section and all its lessons and content?')) return;
  try {
    await fetchAPI(`/api/v1/trainer/modules/${moduleId}`, { method: 'DELETE' });
    showToast('Section deleted');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};

// ── Submodule edit / delete ─────────────────────────────────────

window.openEditSubmoduleModal = function(submoduleId, currentTitle) {
  const newTitle = prompt('Edit lesson title:', currentTitle);
  if (newTitle === null || newTitle.trim() === '') return;
  
  fetchAPI(`/api/v1/trainer/submodules/${submoduleId}`, {
    method: 'PUT',
    body: JSON.stringify({ title: newTitle.trim(), sortOrder: 1, estMinutes: null })
  }).then(() => {
    showToast('Lesson updated');
    loadCourseDetail(state.selectedCourseId);
  }).catch(err => showToast(err.message, 'error'));
};

window.deleteSubmodule = async function(submoduleId) {
  if (!confirm('Delete this lesson and all its content blocks?')) return;
  try {
    await fetchAPI(`/api/v1/trainer/submodules/${submoduleId}`, { method: 'DELETE' });
    showToast('Lesson deleted');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};

// ── Content delete ──────────────────────────────────────────────

window.editContentSortOrder = async function(contentId, currentSortOrder) {
  const input = prompt('Edit content block order (sortOrder):', String(currentSortOrder));
  if (input === null) return;
  const next = parseInt(input);
  if (Number.isNaN(next)) {
    showToast('Invalid sortOrder number', 'error');
    return;
  }
  try {
    // Backend endpoint is expected to exist for content update incl. sortOrder
    await fetchAPI(`/api/v1/trainer/content/${contentId}`, {
      method: 'PUT',
      body: JSON.stringify({ sortOrder: next })
    });
    showToast('Content order updated');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};

window.editContentHeadingLevel = async function(contentId, currentHeadingLevel) {
  const input = prompt('Edit content heading level (headingLevel). Use empty for none:', currentHeadingLevel || '');
  if (input === null) return;
  const next = input.trim();
  try {
    await fetchAPI(`/api/v1/trainer/content/${contentId}`, {
      method: 'PUT',
      body: JSON.stringify({ headingLevel: next || null })
    });
    showToast('Content heading updated');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};

window.deleteContent = async function(contentId) {
  if (!confirm('Delete this content block?')) return;
  try {
    await fetchAPI(`/api/v1/trainer/content/${contentId}`, { method: 'DELETE' });
    showToast('Content block deleted');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};


// ═══════════════════════════════════════════════════════════════
// BATCH ROSTER — API & Rendering
// ═══════════════════════════════════════════════════════════════

async function loadBatches() {
  try {
    const list = await fetchAPI('/api/v1/trainer/batches');
    state.batches = list;
    renderBatchesList();
    
    // Auto-select first batch if none is active and batches exist
    if (!state.selectedBatchId && list.length > 0) {
      selectBatch(list[0].batchId);
    } else if (state.selectedBatchId) {
      // Re-load learners for currently selected batch
      loadBatchLearners(state.selectedBatchId);
    }
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function loadBatchLearners(batchId) {
  try {
    const [learners, feedback] = await Promise.all([
      fetchAPI(`/api/v1/trainer/batches/${batchId}/learners`),
      fetchAPI(`/api/v1/trainer/batches/${batchId}/feedback`)
    ]);
    state.batchLearners = learners;
    state.batchFeedback = feedback;
    renderBatchWorkspace();
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function selectBatch(batchId) {
  state.selectedBatchId = batchId;
  renderBatchesList();
  loadBatchLearners(batchId);
}


async function handleCreateBatch(e) {
  e.preventDefault();
  const name = document.getElementById('batch-name').value.trim();
  const startDateStr = document.getElementById('batch-start-date').value;
  const endDateStr = document.getElementById('batch-end-date').value;
  
  if (!name || !startDateStr || !endDateStr) {
    showToast('All fields are required', 'error');
    return;
  }
  
  try {
    const newBatch = await fetchAPI('/api/v1/trainer/batches', {
      method: 'POST',
      body: JSON.stringify({
        name,
        startDate: new Date(startDateStr).toISOString(),
        endDate: new Date(endDateStr).toISOString()
      })
    });
    
    showToast('Batch created successfully');
    closeModal('modal-batch');
    await loadBatches();
    selectBatch(newBatch.batchId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function handleAddLearner(e) {
  e.preventDefault();
  const name = document.getElementById('learner-name-input').value.trim();
  const email = document.getElementById('learner-email-input').value.trim();
  
  if (!name || !email) {
    showToast('All fields are required', 'error');
    return;
  }
  
  try {
    await fetchAPI(`/api/v1/trainer/batches/${state.selectedBatchId}/learners`, {
      method: 'POST',
      body: JSON.stringify({ name, email })
    });
    
    showToast('Learner enrolled successfully');
    closeModal('modal-add-learner');
    loadBatchLearners(state.selectedBatchId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

function exportRosterToCSV() {
  const batch = state.batches.find(b => b.batchId === state.selectedBatchId);
  if (!batch || state.batchLearners.length === 0) return;
  
  let csvContent = "data:text/csv;charset=utf-8,";
  csvContent += "Learner Name,Email,Joined Date\n";
  
  state.batchLearners.forEach(l => {
    const joinedDate = l.createdAt ? new Date(l.createdAt).toLocaleDateString() : '';
    csvContent += `"${l.name.replace(/"/g, '""')}","${l.email.replace(/"/g, '""')}","${joinedDate}"\n`;
  });
  
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", `${batch.name.replace(/\s+/g, '_')}_roster.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

function getBatchStatus(startDateStr, endDateStr) {
  if (!startDateStr || !endDateStr) return { label: 'Unknown', class: 'badge-level' };
  const now = new Date();
  const start = new Date(startDateStr);
  const end = new Date(endDateStr);
  
  if (now < start) {
    return { label: 'Upcoming', class: 'badge-draft' };
  } else if (now > end) {
    return { label: 'Completed', class: 'badge-published' };
  } else {
    return { label: 'Active', class: 'badge-emerald' };
  }
}

function renderBatchesList() {
  const container = document.getElementById('batches-list');
  container.innerHTML = '';
  
  if (state.batches.length === 0) {
    container.innerHTML = '<div style="color:var(--text-muted);text-align:center;padding:20px;font-size:0.9rem">No batches assigned to you yet.</div>';
    return;
  }
  
  state.batches.forEach(batch => {
    const isActive = batch.batchId === state.selectedBatchId;
    const item = document.createElement('div');
    item.className = `batch-item ${isActive ? 'active' : ''}`;
    item.onclick = () => selectBatch(batch.batchId);
    
    const startDate = batch.startDate ? new Date(batch.startDate).toLocaleDateString() : '—';
    const endDate = batch.endDate ? new Date(batch.endDate).toLocaleDateString() : '—';
    
    const status = getBatchStatus(batch.startDate, batch.endDate);
    
    item.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 8px;">
        <div class="batch-item-title">${escapeHTML(batch.name)}</div>
        <span class="badge ${status.class}" style="font-size: 0.6rem; padding: 2px 6px; flex-shrink: 0;">${status.label}</span>
      </div>
      <div class="batch-item-meta">
        <span class="batch-date-range">${startDate} → ${endDate}</span>
      </div>
    `;
    
    container.appendChild(item);
  });
}

function renderBatchWorkspace(filterText = '') {
  const container = document.getElementById('batch-workspace');
  container.innerHTML = '';
  
  const batch = state.batches.find(b => b.batchId === state.selectedBatchId);
  if (!batch) {
    container.innerHTML = `
      <div class="placeholder-view">
        <div class="placeholder-icon">👥</div>
        <h3>No Batch Selected</h3>
        <p>Choose a batch from the sidebar to view the learner roster.</p>
      </div>
    `;
    return;
  }
  
  const startDate = batch.startDate ? new Date(batch.startDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) : 'Not set';
  const endDate = batch.endDate ? new Date(batch.endDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) : 'Not set';
  const createdAt = batch.createdAt ? new Date(batch.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) : '—';
  
  // Apply filtering
  const query = filterText.toLowerCase().trim();
  const filteredLearners = state.batchLearners.filter(l => 
    l.name.toLowerCase().includes(query) || l.email.toLowerCase().includes(query)
  );
  
  const learnerCount = state.batchLearners.length;
  const filteredCount = filteredLearners.length;
  
  let html = `
    <div style="max-width: 960px; margin: 0 auto;">
      <!-- Batch Info Card -->
      <div class="batch-info-card">
        <div class="batch-info-header">
          <div>
            <span class="badge badge-emerald" style="margin-bottom: 8px; display: inline-block">Batch</span>
            <h2>${escapeHTML(batch.name)}</h2>
          </div>
          <div style="display: flex; gap: 8px; align-items: center">
            <button class="btn btn-secondary btn-sm" onclick="exportRosterToCSV()">📥 Export CSV</button>
            <button class="btn btn-primary btn-sm" onclick="openModal('modal-add-learner')">+ Enroll Learner</button>
          </div>
        </div>
        <div class="batch-stats">
          <div class="batch-stat-item">
            <span class="batch-stat-label">Batch ID</span>
            <span class="batch-stat-value" style="font-family:var(--font-mono);font-size:0.8rem">${batch.batchId}</span>
          </div>
          <div class="batch-stat-item">
            <span class="batch-stat-label">Start Date</span>
            <span class="batch-stat-value">${startDate}</span>
          </div>
          <div class="batch-stat-item">
            <span class="batch-stat-label">End Date</span>
            <span class="batch-stat-value">${endDate}</span>
          </div>
          <div class="batch-stat-item">
            <span class="batch-stat-label">Total Learners</span>
            <span class="batch-stat-value" style="color:var(--emerald); font-weight:700;">${learnerCount}</span>
          </div>
        </div>
      </div>
      
      <!-- Learner Roster -->
      <div class="learner-roster-section">
        <div class="learner-roster-header">
          <div style="display: flex; align-items: center; gap: 12px;">
            <h3>Learner Roster</h3>
            <span class="learner-count-badge">${filteredCount} displayed</span>
          </div>
          
          <!-- Search Box -->
          <div style="width: 250px;">
            <input type="text" id="learner-search" class="form-control" placeholder="🔍 Search learners..." 
                   value="${escapeHTML(filterText)}" oninput="renderBatchWorkspace(this.value)" 
                   style="padding: 6px 12px; font-size: 0.85rem;">
          </div>
        </div>
  `;
  
  if (learnerCount === 0) {
    html += `
        <div class="batch-empty-state">
          <div class="empty-icon">📋</div>
          <h3 style="margin-bottom: 8px">No Learners Yet</h3>
          <p>This batch doesn't have any learners enrolled yet. Click "+ Enroll Learner" to get started.</p>
        </div>
    `;
  } else if (filteredCount === 0) {
    html += `
        <div class="batch-empty-state">
          <div class="empty-icon">🔍</div>
          <h3 style="margin-bottom: 8px">No Matches Found</h3>
          <p>No learners match the search query "${escapeHTML(filterText)}".</p>
        </div>
    `;
  } else {
    html += `
        <table class="learner-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Learner</th>
              <th>Email</th>
              <th>Joined</th>
              <th style="text-align: right">Action</th>
            </tr>
          </thead>
          <tbody>
    `;
    
    filteredLearners.forEach((learner, idx) => {
      const initials = getInitials(learner.name);
      const joinedDate = learner.createdAt 
        ? new Date(learner.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
        : '—';
      
      html += `
            <tr>
              <td style="color: var(--text-muted); font-weight: 600">${idx + 1}</td>
              <td>
                <div class="learner-identity">
                  <div class="learner-avatar">${initials}</div>
                  <span class="learner-name">${escapeHTML(learner.name)}</span>
                </div>
              </td>
              <td><span class="learner-email">${escapeHTML(learner.email)}</span></td>
              <td><span class="learner-date">${joinedDate}</span></td>
              <td style="text-align: right">
                <button class="btn btn-secondary btn-sm" style="padding: 4px 8px; margin-right: 6px;" onclick="openAddFeedbackModal('${learner.learnerId}', '${escapeHTML(learner.name).replace(/'/g, "\\'")}')">Feedback</button>
                <button class="btn btn-secondary btn-sm" style="padding: 4px 8px; color: var(--danger); border-color: rgba(239, 68, 68, 0.2);" onclick="removeLearnerFromBatch('${batch.batchId}', '${learner.learnerId}', '${escapeHTML(learner.name).replace(/'/g, "\\'")}')">Remove</button>
              </td>
            </tr>
      `;
    });
    
    html += `
          </tbody>
        </table>
    `;
  }
  
  // Feedback Logs section
  let feedbackLogsHtml = `
    <div style="margin-top: 32px;">
      <h3 style="border-bottom:1px solid var(--border-color); padding-bottom: 8px; margin-bottom: 16px;">Qualitative Feedback History</h3>
  `;
  
  if (state.batchFeedback.length === 0) {
    feedbackLogsHtml += `
      <div style="color:var(--text-muted); font-size:0.85rem; padding: 20px; background: rgba(255,255,255,0.01); border:1px solid var(--border-color); border-radius: var(--radius-md);">
        No qualitative feedbacks recorded yet for this batch. Click "Feedback" next to a student to save notes.
      </div>
    `;
  } else {
    state.batchFeedback.forEach(f => {
      // Find learner name
      const lObj = state.batchLearners.find(l => l.learnerId === f.learnerId);
      const name = lObj ? lObj.name : 'Unknown Student';
      const initials = getInitials(name);
      const dateStr = f.createdAt ? new Date(f.createdAt).toLocaleString() : '—';
      
      feedbackLogsHtml += `
        <div class="feedback-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 16px; margin-bottom: 12px;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 0.8rem; color: var(--text-muted);">
            <div style="display: flex; align-items: center; gap: 8px;">
              <div class="learner-avatar" style="width: 24px; height: 24px; font-size: 0.6rem;">${initials}</div>
              <strong style="color: var(--text-main);">${escapeHTML(name)}</strong>
            </div>
            <span>${dateStr}</span>
          </div>
          <div style="font-size: 0.9rem; line-height: 1.5; color: var(--text-main); white-space: pre-wrap; padding-left: 32px;">${escapeHTML(f.notes)}</div>
        </div>
      `;
    });
  }
  
  feedbackLogsHtml += `</div>`;
  
  html += `
        ${feedbackLogsHtml}
      </div>
    </div>
  `;
  
  container.innerHTML = html;
  
  // Focus the search input and place cursor at end if it was active
  const searchInput = document.getElementById('learner-search');
  if (searchInput && filterText !== '') {
    searchInput.focus();
    searchInput.setSelectionRange(filterText.length, filterText.length);
  }
}

function getInitials(name) {
  if (!name) return '?';
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

window.openAddFeedbackModal = function(learnerId, learnerName) {
  document.getElementById('feedback-target-learner-id').value = learnerId;
  document.getElementById('feedback-learner-label').textContent = `Feedback Notes for ${learnerName}`;
  document.getElementById('feedback-notes-input').value = '';
  openModal('modal-add-feedback');
};

async function handleCreateFeedback(e) {
  e.preventDefault();
  const learnerId = document.getElementById('feedback-target-learner-id').value;
  const notes = document.getElementById('feedback-notes-input').value.trim();
  
  if (!notes) {
    showToast('Feedback notes cannot be empty', 'error');
    return;
  }
  
  try {
    await fetchAPI(`/api/v1/trainer/batches/${state.selectedBatchId}/learners/${learnerId}/feedback`, {
      method: 'POST',
      body: JSON.stringify({ notes })
    });
    
    showToast('Feedback saved successfully');
    closeModal('modal-add-feedback');
    loadBatchLearners(state.selectedBatchId);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

window.removeLearnerFromBatch = async function(batchId, learnerId, learnerName) {
  if (!confirm(`Are you sure you want to remove ${learnerName} from this batch?`)) {
    return;
  }
  
  try {
    await fetchAPI(`/api/v1/trainer/batches/${batchId}/learners/${learnerId}`, {
      method: 'DELETE'
    });
    showToast('Learner removed successfully');
    loadBatchLearners(batchId);
  } catch (error) {
    showToast(error.message, 'error');
  }
};

// ── Category delete ───────────────────────────────────────────
// Uses backend/API rules enforced in CategoryService.delete:
// - 404 if not found
// - 409/400 style error if the category has courses (cannot delete)
//
// Also keeps UI state consistent by refreshing categories and courses.
window.deleteCategory = async function(categoryId, courseCount = 0) {
  const category = getCategoryById(categoryId);
  const name = category?.name || 'this category';

  if (courseCount > 0) {
    showToast(`Cannot delete ${name}: it contains ${courseCount} course(s).`, 'error');
    return;
  }

  if (!confirm(`Delete ${name}? This cannot be undone.`)) return;

  try {
    await fetchAPI(`/api/v1/trainer/categories/${categoryId}`, { method: 'DELETE' });
    showToast('Category deleted successfully');

    // Refresh lists and keep selection stable
    await Promise.all([loadCategories(), loadCourses()]);

    if (state.selectedCategoryId === categoryId) {
      // loadCategories() will auto-pick first available category, but ensure workspace refresh
      renderCoursesList();
      syncSelectedCourseWithCategory();
    }
  } catch (error) {
    showToast(error.message, 'error');
  }
};

// -------------------------------------------------------------
// Content Reordering Methods
// -------------------------------------------------------------

async function moveContentUp(submoduleId, currentIdx) {
  const detail = state.selectedCourseDetail;
  let sub = null;
  for(let m of detail.modules) {
     sub = m.submodules.find(s => s.submoduleId === submoduleId);
     if(sub) break;
  }
  if (!sub || currentIdx === 0) return;
  
  const orderedIds = sub.contentBlocks.map(c => c.contentId);
  const temp = orderedIds[currentIdx - 1];
  orderedIds[currentIdx - 1] = orderedIds[currentIdx];
  orderedIds[currentIdx] = temp;
  
  await reorderContent(submoduleId, orderedIds);
}

async function moveContentDown(submoduleId, currentIdx) {
  const detail = state.selectedCourseDetail;
  let sub = null;
  for(let m of detail.modules) {
     sub = m.submodules.find(s => s.submoduleId === submoduleId);
     if(sub) break;
  }
  if (!sub || currentIdx === sub.contentBlocks.length - 1) return;
  
  const orderedIds = sub.contentBlocks.map(c => c.contentId);
  const temp = orderedIds[currentIdx + 1];
  orderedIds[currentIdx + 1] = orderedIds[currentIdx];
  orderedIds[currentIdx] = temp;
  
  await reorderContent(submoduleId, orderedIds);
}

async function reorderContent(submoduleId, orderedIds) {
  try {
    await fetchAPI(`/api/v1/trainer/submodules/${submoduleId}/content/reorder`, {
      method: 'PUT',
      body: JSON.stringify(orderedIds)
    });
    showToast('Block moved successfully');
    loadCourseDetail(state.selectedCourseId);
  } catch (error) {
    showToast('Failed to reorder blocks: ' + error.message, 'error');
  }
}

// -------------------------------------------------------------
// Local File Upload
// -------------------------------------------------------------
window.handleLocalUploadEvent = async function(input) {
  if (!input.files || input.files.length === 0) return;
  const file = input.files[0];
  const statusSpan = document.getElementById('local-upload-status');
  statusSpan.textContent = 'Uploading...';
  
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const res = await fetch('/api/v1/trainer/upload/local', {
      method: 'POST',
      headers: {
        'X-Trainer-Id': state.trainerId
      },
      body: formData
    });
    
    if (!res.ok) {
      let errMsg = 'Upload failed';
      try {
         const errJson = await res.json();
         if (errJson.message) errMsg = errJson.message;
      } catch(e) {}
      throw new Error(errMsg);
    }
    const data = await res.json();
    
    document.getElementById('resolved-s3-key').value = data.url;
    document.getElementById('manual-s3-key').value = data.url;
    statusSpan.textContent = 'Uploaded successfully!';
    statusSpan.style.color = 'var(--emerald)';
  } catch (error) {
    statusSpan.textContent = 'Failed: ' + error.message;
    statusSpan.style.color = 'var(--danger)';
  }
};

function setupCategoryImageUploader() {
  const fileInput = document.getElementById('category-image-file');
  const uploadWrapper = document.getElementById('category-upload-wrapper');
  const progressBar = document.getElementById('category-image-progress');
  const progressFill = document.getElementById('category-image-progress-fill');
  const fileInfo = document.getElementById('category-image-info');
  const resolvedKeyInput = document.getElementById('category-icon');
  
  if (!uploadWrapper) return;
  
  uploadWrapper.addEventListener('click', () => fileInput.click());
  
  fileInput.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    // UI Update
    fileInfo.textContent = `Uploading: ${file.name}`;
    progressBar.style.display = 'block';
    progressFill.style.width = '0%';
    resolvedKeyInput.value = '';
    
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const xhr = new XMLHttpRequest();
      xhr.open('POST', '/api/v1/trainer/upload/local', true);
      xhr.setRequestHeader('X-Trainer-Id', state.trainerId);
      
      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          const percent = Math.round((event.loaded / event.total) * 100);
          progressFill.style.width = `${percent}%`;
        }
      };
      
      xhr.onload = () => {
        if (xhr.status === 200 || xhr.status === 201) {
          try {
            const data = JSON.parse(xhr.responseText);
            showToast('Image uploaded successfully');
            fileInfo.innerHTML = `✅ Uploaded!<br><span style="font-size:0.75rem;color:var(--text-muted)">Path: ${data.url}</span>`;
            resolvedKeyInput.value = data.url;
          } catch (e) {
            showToast('Failed to parse upload response', 'error');
          }
        } else {
          showToast(`Upload failed: ${xhr.status} ${xhr.statusText}`, 'error');
        }
      };
      
      xhr.onerror = () => {
        showToast('Upload network connection failed.', 'error');
      };
      
      xhr.send(formData);
      
    } catch (error) {
      console.error(error);
      showToast('Upload failed: ' + error.message, 'error');
      progressBar.style.display = 'none';
      fileInfo.innerHTML = `❌ Upload error: ${error.message}`;
    }
  });
}

function resetCategoryImageUpload() {
  const fileInput = document.getElementById('category-image-file');
  if (fileInput) fileInput.value = '';
  const fileInfo = document.getElementById('category-image-info');
  if (fileInfo) fileInfo.textContent = 'Click to upload image';
  const progressBar = document.getElementById('category-image-progress');
  if (progressBar) progressBar.style.display = 'none';
  const progressFill = document.getElementById('category-image-progress-fill');
  if (progressFill) progressFill.style.width = '0%';
  const resolvedKeyInput = document.getElementById('category-icon');
  if (resolvedKeyInput) resolvedKeyInput.value = '';
}
