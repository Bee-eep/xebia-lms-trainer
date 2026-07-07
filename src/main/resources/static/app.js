// State Management
let state = {
  courses: [],
  selectedCourseId: null,
  selectedCourseDetail: null,
  trainerId: getOrCreateTrainerId(),
  defaultDomainId: 'd6b5e672-0000-4000-a000-000000000001', // Mock default domain UUID
  activeTab: 'authoring',
  evaluations: [], // DB evaluations history
  selectedSubmissionId: null,
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
function getOrCreateTrainerId() {
  let id = localStorage.getItem('lms_trainer_id');
  if (!id) {
    id = generateUUID();
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
  document.getElementById('btn-new-course').addEventListener('click', () => openModal('modal-course'));
  document.getElementById('form-course').addEventListener('submit', handleCreateCourse);
  document.getElementById('form-edit-course').addEventListener('submit', handleEditCourse);
  document.getElementById('form-module').addEventListener('submit', handleCreateModule);
  document.getElementById('form-submodule').addEventListener('submit', handleCreateSubmodule);
  document.getElementById('form-content').addEventListener('submit', handleCreateContent);
  
  // Wire Content Type conditional inputs in add content form
  document.getElementById('content-type').addEventListener('change', handleContentTypeChange);
  
  // Wire S3 File uploader
  setupFileUploader();

  // Load Initial Data
  loadCourses();
});

function isValidUUID(uuid) {
  const regex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
  return regex.test(uuid);
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
  return await response.json();
}

// Fetch lists of courses
async function loadCourses() {
  try {
    const list = await fetchAPI('/api/v1/trainer/courses');
    state.courses = list;
    renderCoursesList();
    
    // Select first course if none is active and courses exist
    if (!state.selectedCourseId && list.length > 0) {
      selectCourse(list[0].courseId);
    }
  } catch (error) {
    showToast(error.message, 'error');
  }
}

// Load Course Detailed Tree
async function loadCourseDetail(courseId) {
  try {
    const detail = await fetchAPI(`/api/v1/trainer/courses/${courseId}`);
    state.selectedCourseDetail = detail;
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
  
  try {
    const newCourse = await fetchAPI('/api/v1/trainer/courses', {
      method: 'POST',
      body: JSON.stringify({
        domainId: state.defaultDomainId,
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
  const type = document.getElementById('content-type').value;
  const body = document.getElementById('content-body').value.trim();
  const language = document.getElementById('content-language').value;
  const sortOrder = parseInt(document.getElementById('content-sort').value) || 1;
  
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
    await fetchAPI(`/api/v1/trainer/submodules/${submoduleId}/content`, {
      method: 'POST',
      body: JSON.stringify({
        type,
        body: ['TEXT', 'CODE'].includes(type) ? body : null,
        s3Key,
        language: type === 'CODE' ? language : null,
        sortOrder
      })
    });
    
    showToast('Content block added successfully');
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

function renderCoursesList() {
  const container = document.getElementById('courses-list');
  container.innerHTML = '';
  
  if (state.courses.length === 0) {
    container.innerHTML = '<div style="color:var(--text-muted);text-align:center;padding:20px;font-size:0.9rem">No courses found</div>';
    return;
  }
  
  state.courses.forEach(course => {
    const isActive = course.courseId === state.selectedCourseId;
    const item = document.createElement('div');
    item.className = `course-item ${isActive ? 'active' : ''}`;
    item.onclick = () => selectCourse(course.courseId);
    
    item.innerHTML = `
      <div class="course-item-title">${escapeHTML(course.title)}</div>
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
      <div class="placeholder-view">
        <div class="placeholder-icon">🗂️</div>
        <h3>No Course Selected</h3>
        <p>Choose an existing course from the sidebar or build a new one to start authoring.</p>
      </div>
    `;
    return;
  }
  
  const isPublished = detail.status === 'PUBLISHED';
  
  // Render Course header & modules
  let html = `
    <div class="course-detail">
      <div class="course-meta-card">
        <div class="course-meta-header">
          <div class="course-title-section">
            <span class="badge badge-level" style="margin-bottom:8px;display:inline-block">Level: ${escapeHTML(detail.level)}</span>
            <h2>${escapeHTML(detail.title)}</h2>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
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
              <button class="btn btn-secondary btn-sm" onclick="deleteModule('${mod.moduleId}')" style="color:#ef4444">🗑️</button>
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
                  <button class="btn btn-secondary btn-sm" style="padding:4px 8px;font-size:0.75rem;color:#ef4444" onclick="deleteSubmodule('${sub.submoduleId}')">🗑️</button>
                </div>
              </div>
              <div class="content-blocks-list">
          `;
          
          if (!sub.contentBlocks || sub.contentBlocks.length === 0) {
            html += `
                <div style="color:var(--text-muted);font-size:0.8rem;text-align:center;padding:10px">No content blocks.</div>
            `;
          } else {
            sub.contentBlocks.forEach(content => {
              html += `
                <div class="content-block">
                  <div class="content-block-header">
                    <span class="content-block-type">${content.type}</span>
                    <div style="display:flex;align-items:center;gap:8px">
                      <span>Order: ${content.sortOrder}</span>
                      <button class="btn btn-secondary btn-sm" style="padding:2px 6px;font-size:0.7rem;color:#ef4444" onclick="deleteContent('${content.contentId}')">🗑️</button>
                    </div>
                  </div>
              `;
              
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
  document.getElementById('target-submodule-id').value = submoduleId;
  openModal('modal-content');
}

// Content Type Conditional Rendering
function handleContentTypeChange(e) {
  const type = e.target.value;
  const textGroup = document.getElementById('group-body');
  const codeGroup = document.getElementById('group-language');
  const mediaGroup = document.getElementById('group-media');
  
  if (type === 'TEXT') {
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
      // 1. Get presigned upload URL
      const presignResponse = await fetchAPI(`/api/v1/trainer/content/media/presign?fileName=${encodeURIComponent(file.name)}&contentType=${encodeURIComponent(file.type)}`, {
        method: 'POST'
      });
      
      const { uploadUrl, s3Key } = presignResponse;
      fileInfo.textContent = `Uploading S3 key: ${s3Key}...`;
      
      // 2. Upload directly to S3 PUT URL using raw XMLHttpRequest to track progress
      const xhr = new XMLHttpRequest();
      xhr.open('PUT', uploadUrl, true);
      xhr.setRequestHeader('Content-Type', file.type);
      
      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          const percent = Math.round((event.loaded / event.total) * 100);
          progressFill.style.width = `${percent}%`;
        }
      };
      
      xhr.onload = () => {
        if (xhr.status === 200 || xhr.status === 204) {
          showToast('File uploaded to S3 successfully');
          fileInfo.innerHTML = `✅ Uploaded!<br><span style="font-size:0.75rem;color:var(--text-muted)">Key: ${s3Key}</span>`;
          resolvedKeyInput.value = s3Key;
        } else {
          throw new Error(`S3 upload failed: ${xhr.status} ${xhr.statusText}`);
        }
      };
      
      xhr.onerror = () => {
        throw new Error('S3 upload network connection failed.');
      };
      
      xhr.send(file);
      
    } catch (error) {
      console.error(error);
      showToast('S3 Presign/Upload failed. See details below.', 'warning');
      progressBar.style.display = 'none';
      fallbackBox.style.display = 'block';
      fileInfo.innerHTML = `❌ S3 Presign/Upload error: ${error.message}`;
      
      // Generate a mock key for offline fallback testing
      const mockKey = `content/${generateUUID()}-${file.name}`;
      resolvedKeyInput.value = mockKey;
      document.getElementById('manual-s3-key').value = mockKey;
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
  const viewAuthoring = document.getElementById('view-authoring');
  const viewEvaluations = document.getElementById('view-evaluations');
  
  if (tab === 'authoring') {
    menuAuthoring.classList.add('active');
    menuEvaluations.classList.remove('active');
    viewAuthoring.style.display = 'flex';
    viewEvaluations.style.display = 'none';
    loadCourses();
  } else {
    menuAuthoring.classList.remove('active');
    menuEvaluations.classList.add('active');
    viewAuthoring.style.display = 'none';
    viewEvaluations.style.display = 'flex';
    loadEvaluations();
    renderRoster();
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
  
  document.getElementById('edit-course-title').value = detail.title;
  document.getElementById('edit-course-summary-input').value = detail.summary || '';
  document.getElementById('edit-course-level').value = detail.level;
  
  openModal('modal-edit-course');
};

window.handleEditCourse = async function(e) {
  e.preventDefault();
  if (!state.selectedCourseId) return;
  
  const title = document.getElementById('edit-course-title').value.trim();
  const summary = document.getElementById('edit-course-summary-input').value.trim();
  const level = document.getElementById('edit-course-level').value;
  
  try {
    const updated = await fetchAPI(`/api/v1/trainer/courses/${state.selectedCourseId}`, {
      method: 'PUT',
      body: JSON.stringify({
        domainId: state.defaultDomainId,
        title,
        summary,
        level
      })
    });
    
    showToast('Course metadata updated successfully');
    closeModal('modal-edit-course');
    await loadCourses();
    selectCourse(state.selectedCourseId);
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
