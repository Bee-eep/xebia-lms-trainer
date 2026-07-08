document.addEventListener('DOMContentLoaded', () => {
  // Setup Marked.js with Highlight.js
  marked.setOptions({
    highlight: function(code, lang) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext';
      return hljs.highlight(code, { language }).value;
    },
    langPrefix: 'hljs language-',
    breaks: true
  });

  const urlParams = new URLSearchParams(window.location.search);
  const courseId = urlParams.get('courseId');

  if (!courseId) {
    document.getElementById('course-title').textContent = 'Error: No Course ID';
    document.getElementById('course-summary').textContent = 'Please provide a courseId in the URL parameters.';
    return;
  }

  loadCourseData(courseId);
});

// Common fetch utility
async function fetchAPI(url) {
  const trainerId = localStorage.getItem('lms_trainer_id') || '00000000-0000-4000-a000-000000000001';
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      'X-Trainer-Id': trainerId
    }
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status} ${response.statusText}`);
  }

  if (response.status === 204) return null;
  return await response.json();
}

async function loadCourseData(courseId) {
  try {
    const course = await fetchAPI(`/api/v1/trainer/courses/${courseId}`);
    
    document.getElementById('course-title').textContent = course.title;
    document.getElementById('course-summary').textContent = course.summary || 'No summary provided.';
    
    renderSidebar(course);
    
    // Automatically select the first lesson if available
    if (course.modules && course.modules.length > 0) {
      const firstModule = course.modules[0];
      if (firstModule.submodules && firstModule.submodules.length > 0) {
        selectLesson(firstModule.submodules[0].submoduleId, firstModule.submodules[0].title);
      }
    }
  } catch (error) {
    document.getElementById('course-title').textContent = 'Error Loading Course';
    document.getElementById('course-summary').textContent = error.message;
    console.error('Failed to load course:', error);
  }
}

function renderSidebar(course) {
  const navContainer = document.getElementById('course-nav');
  navContainer.innerHTML = '';

  if (!course.modules || course.modules.length === 0) {
    navContainer.innerHTML = '<div style="padding: 24px; color: var(--text-muted); font-size: 0.9rem;">No modules found in this course.</div>';
    return;
  }

  course.modules.forEach((mod, modIdx) => {
    // Module Header
    const modDiv = document.createElement('div');
    modDiv.className = 'module-nav-item';
    modDiv.innerHTML = `<div class="module-nav-title">Section ${modIdx + 1}: ${escapeHTML(mod.title)}</div>`;
    navContainer.appendChild(modDiv);

    // Submodules (Lessons)
    if (mod.submodules && mod.submodules.length > 0) {
      mod.submodules.forEach((sub, subIdx) => {
        const subDiv = document.createElement('div');
        subDiv.className = 'lesson-nav-item';
        subDiv.id = `nav-lesson-${sub.submoduleId}`;
        subDiv.textContent = `${modIdx + 1}.${subIdx + 1} ${sub.title}`;
        
        subDiv.addEventListener('click', () => {
          selectLesson(sub.submoduleId, sub.title);
        });
        
        navContainer.appendChild(subDiv);
        
        // Render headings if they exist for navigation
        if (sub.contentBlocks && sub.contentBlocks.length > 0) {
           const hasHeadings = sub.contentBlocks.some(b => b.headingText);
           if (hasHeadings) {
             const contentList = document.createElement('div');
             contentList.className = 'content-nav-list';
             contentList.id = `nav-content-list-${sub.submoduleId}`;
             contentList.style.display = 'none'; // Hidden by default, shown when active
             
             sub.contentBlocks.forEach(block => {
                if (block.headingText) {
                   const itemDiv = document.createElement('div');
                   itemDiv.className = 'content-nav-item';
                   itemDiv.textContent = block.headingText;
                   itemDiv.addEventListener('click', (e) => {
                      e.stopPropagation();
                      selectLesson(sub.submoduleId, sub.title).then(() => {
                         // Scroll to the content block
                         setTimeout(() => {
                            const target = document.getElementById(`block-${block.contentId}`);
                            if (target) {
                               const scrollContainer = document.getElementById('viewer-main-scroll');
                               if(scrollContainer) {
                                  // Accounts for sticky header
                                  const headerOffset = 100;
                                  const elementPosition = target.getBoundingClientRect().top;
                                  const offsetPosition = elementPosition + scrollContainer.scrollTop - headerOffset;
                                  scrollContainer.scrollTo({
                                     top: offsetPosition,
                                     behavior: "smooth"
                                  });
                               } else {
                                  target.scrollIntoView({behavior: 'smooth'});
                               }
                            }
                         }, 100);
                         
                         // Update active state for content items
                         document.querySelectorAll('.content-nav-item').forEach(el => el.classList.remove('active'));
                         itemDiv.classList.add('active');
                      });
                   });
                   contentList.appendChild(itemDiv);
                }
             });
             navContainer.appendChild(contentList);
           }
        }
      });
    } else {
      const emptyDiv = document.createElement('div');
      emptyDiv.className = 'lesson-nav-item';
      emptyDiv.style.cursor = 'default';
      emptyDiv.textContent = 'No lessons yet';
      navContainer.appendChild(emptyDiv);
    }
  });
}

async function selectLesson(submoduleId, title) {
  // Update Active State for lessons
  document.querySelectorAll('.lesson-nav-item').forEach(el => el.classList.remove('active'));
  const activeEl = document.getElementById(`nav-lesson-${submoduleId}`);
  if (activeEl) {
    activeEl.classList.add('active');
  }
  
  // Hide all content lists, show the one for the active lesson
  document.querySelectorAll('.content-nav-list').forEach(el => el.style.display = 'none');
  const activeContentList = document.getElementById(`nav-content-list-${submoduleId}`);
  if (activeContentList) {
     activeContentList.style.display = 'flex';
  }

  document.getElementById('lesson-title').textContent = title;
  const contentArea = document.getElementById('lesson-content');
  
  contentArea.innerHTML = '<div class="empty-state"><h3>Loading content...</h3></div>';

  try {
    const contents = await fetchAPI(`/api/v1/trainer/submodules/${submoduleId}/content/preview`);
    renderContentBlocks(contents);
  } catch (error) {
    contentArea.innerHTML = `<div class="empty-state"><h3>Error loading lesson</h3><p>${escapeHTML(error.message)}</p></div>`;
  }
}

function renderContentBlocks(contents) {
  const contentArea = document.getElementById('lesson-content');
  contentArea.innerHTML = '';

  if (!contents || contents.length === 0) {
    contentArea.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">📝</div>
        <h3>No Content Found</h3>
        <p>This lesson doesn't have any content blocks yet.</p>
      </div>
    `;
    return;
  }

  contents.forEach(block => {
    const blockDiv = document.createElement('div');
    blockDiv.className = 'content-block';
    if (block.contentId) {
        blockDiv.id = `block-${block.contentId}`;
    }

    // Note: User requested not to show heading text directly in the content block if it's meant for the sidebar.
    // So we skip rendering the block.headingLevel & block.headingText here.

    // Render Body based on type
    const bodyDiv = document.createElement('div');
    
    switch (block.type) {
      case 'HEADING':
        bodyDiv.className = 'content-heading';
        const level = block.headingLevel || 'H2';
        bodyDiv.innerHTML = `<${level.toLowerCase()}>${escapeHTML(block.body || '')}</${level.toLowerCase()}>`;
        break;
        
      case 'QUOTE':
        bodyDiv.className = 'content-quote content-markdown';
        bodyDiv.innerHTML = `<blockquote>${marked.parse(block.body || '')}</blockquote>`;
        break;
        
      case 'TEXT':
        bodyDiv.className = 'content-markdown';
        bodyDiv.innerHTML = marked.parse(block.body || '');
        break;
        
      case 'CODE':
        bodyDiv.className = 'content-markdown';
        const lang = block.language || 'plaintext';
        // Format as markdown code block and parse it to get hljs highlighting automatically
        const mdCode = `\`\`\`${lang}\n${block.body || ''}\n\`\`\``;
        bodyDiv.innerHTML = marked.parse(mdCode);
        break;
        
      case 'IMAGE':
        const imgUrl = block.url || block.s3Key;
        if (imgUrl) {
          bodyDiv.innerHTML = `<img src="${escapeHTML(imgUrl)}" class="content-media-image" alt="Lesson Image" />`;
        } else {
          bodyDiv.innerHTML = '<div class="empty-state"><p>Image missing</p></div>';
        }
        break;
        
      case 'VIDEO':
        const vidUrl = block.url || block.s3Key;
        if (vidUrl) {
          bodyDiv.innerHTML = `
            <video class="content-media-video" controls>
              <source src="${escapeHTML(vidUrl)}" type="video/mp4">
              Your browser does not support the video tag.
            </video>
          `;
        } else {
          bodyDiv.innerHTML = '<div class="empty-state"><p>Video missing</p></div>';
        }
        break;
        
      case 'PDF':
        const pdfUrl = block.url || block.s3Key;
        if (pdfUrl) {
          bodyDiv.innerHTML = `<iframe src="${escapeHTML(pdfUrl)}" class="content-media-pdf"></iframe>`;
        } else {
          bodyDiv.innerHTML = '<div class="empty-state"><p>PDF missing</p></div>';
        }
        break;
        
      default:
        bodyDiv.innerHTML = `<p>Unsupported content type: ${escapeHTML(block.type)}</p>`;
    }
    
    blockDiv.appendChild(bodyDiv);
    contentArea.appendChild(blockDiv);
  });
}

function escapeHTML(str) {
  if (!str) return '';
  return str.replace(/[&<>'"]/g, 
    tag => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        "'": '&#39;',
        '"': '&quot;'
      }[tag])
  );
}

