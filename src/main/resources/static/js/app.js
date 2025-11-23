// Global state
let currentUser = null;
let categories = [];
let projects = [];

// API Base URL
const API_BASE = 'http://localhost:8185/api';

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadCategories();
    loadProjects();
    setupEventListeners();
});

// Event Listeners
function setupEventListeners() {
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('registerForm').addEventListener('submit', handleRegister);
    document.getElementById('createProjectForm').addEventListener('submit', handleCreateProject);
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchProjects();
        }
    });
}

// Load Categories
async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE}/categories`);
        categories = await response.json();
        populateCategorySelect();
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

function populateCategorySelect() {
    const select = document.getElementById('projectCategory');
    select.innerHTML = '<option value="">Select Category</option>';
    categories.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat.categoryId;
        option.textContent = cat.name;
        select.appendChild(option);
    });
}

// Load Projects
async function loadProjects(keyword = '', categoryName = '') {
    showLoading(true);
    try {
        const params = new URLSearchParams();
        if (keyword) params.append('keyword', keyword);
        if (categoryName) params.append('category', categoryName);
        
        const url = `${API_BASE}/projects${params.toString() ? '?' + params.toString() : ''}`;
        const response = await fetch(url);
        projects = await response.json();
        displayProjects(projects);
    } catch (error) {
        console.error('Error loading projects:', error);
        showAlert('Error loading projects', 'danger');
    } finally {
        showLoading(false);
    }
}

function displayProjects(projectsList) {
    const grid = document.getElementById('projectsGrid');
    if (projectsList.length === 0) {
        grid.innerHTML = '<div class="col-12"><div class="alert alert-info">No projects found.</div></div>';
        return;
    }

    grid.innerHTML = projectsList.map(project => `
        <div class="col-md-4 mb-4">
            <div class="card project-card" onclick="showProjectDetails('${project.projectId}')">
                ${getProjectImage(project)}
                <div class="card-body">
                    <h5 class="card-title">${escapeHtml(project.title)}</h5>
                    <p class="card-text">${escapeHtml(project.description || '').substring(0, 100)}...</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="badge bg-primary">${escapeHtml(project.categoryName)}</span>
                        <small class="text-muted">${formatDate(project.createdAt)}</small>
                    </div>
                    <div class="mt-2 d-flex justify-content-between">
                        <span><i class="bi bi-chat"></i> ${project.commentCount || 0} comments</span>
                        <span><i class="bi bi-heart"></i> ${project.upVoteCount || 0} upvotes</span>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

function getProjectImage(project) {
    const imageFile = project.files?.find(f => f.fileType?.startsWith('image/'));
    if (imageFile) {
        return `<img src="${API_BASE}/files/${imageFile.filePath}" class="card-img-top" alt="${escapeHtml(project.title)}">`;
    }
    return '<div class="card-img-top bg-secondary d-flex align-items-center justify-content-center" style="height: 200px;"><span class="text-white">No Image</span></div>';
}

// Search and Filter
function searchProjects() {
    const keyword = document.getElementById('searchInput').value.trim();
    const selectedCategories = Array.from(document.querySelectorAll('.category-filter:checked'))
        .map(cb => cb.value);
    
    if (selectedCategories.length > 0) {
        // If multiple categories selected, search with first one (or modify backend to accept array)
        loadProjects(keyword, selectedCategories[0]);
    } else {
        loadProjects(keyword, '');
    }
}

function clearFilters() {
    document.getElementById('searchInput').value = '';
    document.querySelectorAll('.category-filter').forEach(cb => cb.checked = false);
    loadProjects();
}

// Authentication
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        // For now, just store locally. In production, implement proper JWT auth
        const response = await fetch(`${API_BASE}/users/signIn`);
        const users = await response.json();
        const user = users.find(u => u.email === email);
        
        if (user) {
            currentUser = user;
            showAlert('Login successful!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('loginModal')).hide();
            updateUI();
        } else {
            showAlert('Invalid credentials', 'danger');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('Login failed', 'danger');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    try {
        const response = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password, role: 'MEMBER' })
        });

        if (response.ok) {
            const user = await response.json();
            currentUser = user;
            showAlert('Registration successful!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('registerModal')).hide();
            updateUI();
        } else {
            const error = await response.json();
            showAlert(error.error || 'Registration failed', 'danger');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showAlert('Registration failed', 'danger');
    }
}

// Project Management
async function handleCreateProject(e) {
    e.preventDefault();
    
    if (!currentUser) {
        showAlert('Please login first', 'warning');
        return;
    }

    const formData = new FormData();
    formData.append('userId', currentUser.userId);
    formData.append('categoryId', document.getElementById('projectCategory').value);
    formData.append('title', document.getElementById('projectTitle').value);
    formData.append('description', document.getElementById('projectDescription').value);
    
    const files = document.getElementById('projectFiles').files;
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    try {
        const response = await fetch(`${API_BASE}/projects`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            showAlert('Project created successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('createProjectModal')).hide();
            document.getElementById('createProjectForm').reset();
            loadProjects();
        } else {
            const error = await response.json();
            showAlert(error.error || 'Failed to create project', 'danger');
        }
    } catch (error) {
        console.error('Create project error:', error);
        showAlert('Failed to create project', 'danger');
    }
}

async function showProjectDetails(projectId) {
    try {
        const response = await fetch(`${API_BASE}/projects/${projectId}`);
        const project = await response.json();
        
        const commentsResponse = await fetch(`${API_BASE}/comments/project/${projectId}`);
        const comments = await commentsResponse.json();

        const modal = new bootstrap.Modal(document.getElementById('projectDetailsModal'));
        document.getElementById('projectDetailsTitle').textContent = project.title;
        
        const body = document.getElementById('projectDetailsBody');
        body.innerHTML = `
            <div class="mb-3">
                <span class="badge bg-primary">${escapeHtml(project.categoryName)}</span>
                <span class="text-muted ms-2">By ${escapeHtml(project.userEmail)}</span>
                <span class="text-muted ms-2">• ${formatDate(project.createdAt)}</span>
            </div>
            <p>${escapeHtml(project.description || 'No description')}</p>
            
            <div class="mb-3">
                <h6>Files</h6>
                <div class="row">
                    ${project.files?.map(file => `
                        <div class="col-md-6 mb-3">
                            <div class="card">
                                <div class="card-body">
                                    <h6>${escapeHtml(file.fileName)}</h6>
                                    ${file.fileType?.startsWith('image/') 
                                        ? `<img src="${API_BASE}/files/${file.filePath}" class="file-preview" alt="${escapeHtml(file.fileName)}">`
                                        : `<a href="${API_BASE}/files/${file.filePath}" target="_blank" class="btn btn-sm btn-primary">View Code</a>`
                                    }
                                </div>
                            </div>
                        </div>
                    `).join('') || '<p>No files</p>'}
                </div>
            </div>

            <div class="mb-3">
                <button class="btn btn-sm btn-outline-danger upvote-btn" onclick="toggleUpVote('${project.projectId}')">
                    <i class="bi bi-heart"></i> <span id="upvoteCount">${project.upVoteCount || 0}</span>
                </button>
            </div>

            <div class="comment-section">
                <h6>Comments (${comments.length})</h6>
                <div id="commentsList">
                    ${comments.map(comment => `
                        <div class="comment-item">
                            <strong>${escapeHtml(comment.userEmail)}</strong>
                            <small class="text-muted">• ${formatDate(comment.createdAt)}</small>
                            <p>${escapeHtml(comment.commentText)}</p>
                        </div>
                    `).join('')}
                </div>
                ${currentUser ? `
                    <div class="mt-3">
                        <textarea class="form-control" id="newCommentText" rows="2" placeholder="Add a comment..."></textarea>
                        <button class="btn btn-primary mt-2" onclick="addComment('${project.projectId}')">Post Comment</button>
                    </div>
                ` : '<p class="text-muted">Please login to comment</p>'}
            </div>
        `;
        
        modal.show();
    } catch (error) {
        console.error('Error loading project details:', error);
        showAlert('Error loading project details', 'danger');
    }
}

async function toggleUpVote(projectId) {
    if (!currentUser) {
        showAlert('Please login first', 'warning');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/upvotes/toggle`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ projectId, userId: currentUser.userId })
        });

        if (response.ok) {
            const data = await response.json();
            document.getElementById('upvoteCount').textContent = data.upVoteCount;
            loadProjects(); // Refresh list
        }
    } catch (error) {
        console.error('Error toggling upvote:', error);
    }
}

async function addComment(projectId) {
    if (!currentUser) {
        showAlert('Please login first', 'warning');
        return;
    }

    const commentText = document.getElementById('newCommentText').value.trim();
    if (!commentText) {
        showAlert('Please enter a comment', 'warning');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/comments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                projectId,
                userId: currentUser.userId,
                commentText
            })
        });

        if (response.ok) {
            document.getElementById('newCommentText').value = '';
            showProjectDetails(projectId); // Reload details
        } else {
            const error = await response.json();
            showAlert(error.error || 'Failed to add comment', 'danger');
        }
    } catch (error) {
        console.error('Error adding comment:', error);
        showAlert('Failed to add comment', 'danger');
    }
}

// Utility Functions
function showLoginModal() {
    new bootstrap.Modal(document.getElementById('loginModal')).show();
}

function showRegisterModal() {
    new bootstrap.Modal(document.getElementById('registerModal')).show();
}

function showCreateProjectModal() {
    if (!currentUser) {
        showAlert('Please login first', 'warning');
        showLoginModal();
        return;
    }
    new bootstrap.Modal(document.getElementById('createProjectModal')).show();
}

function showLoading(show) {
    document.getElementById('loadingSpinner').style.display = show ? 'block' : 'none';
}

function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    setTimeout(() => alertDiv.remove(), 3000);
}

function updateUI() {
    const nav = document.querySelector('.navbar-nav');
    if (currentUser) {
        nav.innerHTML = `
            <li class="nav-item">
                <span class="nav-link">Welcome, ${currentUser.email}</span>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#" onclick="logout()">Logout</a>
            </li>
        `;
    }
}

function logout() {
    currentUser = null;
    location.reload();
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

