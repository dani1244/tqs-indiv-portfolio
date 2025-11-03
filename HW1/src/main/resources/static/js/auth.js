// API_BASE is defined in config.js
let currentUser = null;
let sessionToken = null;

// Check if user is already logged in on page load
document.addEventListener('DOMContentLoaded', () => {
    checkSession();
});

// Check for existing session
function checkSession() {
    sessionToken = localStorage.getItem('staffSessionToken');

    if (sessionToken) {
        // Validate session with backend
        validateSession();
    } else {
        showLogin();
    }
}

// Validate session token
async function validateSession() {
    try {
        const response = await fetch(`${API_BASE}/auth/validate`, {
            headers: {
                'X-Session-Token': sessionToken
            }
        });

        if (response.ok) {
            const isValid = await response.json();
            if (isValid) {
                // Session is valid, load user permissions
                await loadUserPermissions();
                showMainContent();
            } else {
                // Session invalid, show login
                logout();
            }
        } else {
            logout();
        }
    } catch (error) {
        console.error('Session validation error:', error);
        logout();
    }
}

// Handle login form submission
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    console.log('Login form submitted');

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const loginButton = e.target.querySelector('button[type="submit"]');
    const originalText = loginButton.innerHTML;
    loginButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Autenticando...';
    loginButton.disabled = true;

    try {
        console.log('Attempting login for:', username);
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        console.log('Login response status:', response.status);

        if (!response.ok) {
            throw new Error('Credenciais inválidas');
        }

        const data = await response.json();
        console.log('Login successful:', data);

        // Save session data
        sessionToken = data.sessionToken;
        currentUser = {
            username: data.username,
            fullName: data.fullName,
            role: data.role,
            municipality: data.municipality
        };

        localStorage.setItem('staffSessionToken', sessionToken);
        localStorage.setItem('staffUser', JSON.stringify(currentUser));

        console.log('Loading user permissions...');
        // Load full permissions before showing main content
        await loadUserPermissions();

        console.log('Calling showMainContent()');
        // Show main content
        showMainContent();

    } catch (error) {
        loginButton.innerHTML = originalText;
        loginButton.disabled = false;

        // Show error in the login form
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-error';
        errorDiv.style.marginTop = '15px';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${error.message}`;

        const existingError = document.querySelector('#loginForm .alert');
        if (existingError) {
            existingError.remove();
        }

        document.getElementById('loginForm').appendChild(errorDiv);

        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }
});

// Show login overlay
function showLogin() {
    document.getElementById('loginOverlay').style.display = 'flex';
    document.getElementById('mainContent').style.display = 'none';
}

// Show main content
function showMainContent() {
    console.log('showMainContent() called');
    console.log('Current user:', currentUser);

    const loginOverlay = document.getElementById('loginOverlay');
    const mainContent = document.getElementById('mainContent');

    console.log('loginOverlay element:', loginOverlay);
    console.log('mainContent element:', mainContent);

    if (loginOverlay) {
        loginOverlay.style.display = 'none';
        console.log('Login overlay hidden');
    }

    if (mainContent) {
        mainContent.style.display = 'block';
        console.log('Main content shown');
    }

    // Update user info in header
    const userNameEl = document.getElementById('userName');
    const userRoleEl = document.getElementById('userRole');

    if (userNameEl && currentUser) {
        userNameEl.textContent = currentUser.fullName;
        console.log('User name updated:', currentUser.fullName);
    }

    if (userRoleEl && currentUser) {
        userRoleEl.textContent = currentUser.role;
        console.log('User role updated:', currentUser.role);
    }

    // Update UI based on permissions
    if (typeof updateUIBasedOnPermissions === 'function') {
        console.log('Updating UI based on permissions...');
        updateUIBasedOnPermissions();
    }

    // Load data
    if (typeof loadMunicipalities === 'function') {
        console.log('Loading municipalities...');
        loadMunicipalities();
    }
    if (typeof loadAllBookings === 'function') {
        console.log('Loading bookings...');
        loadAllBookings();
    }
}

// Logout function
async function logout() {
    console.log('logout() called');
    try {
        if (sessionToken) {
            console.log('Calling logout API with token:', sessionToken);
            await fetch(`${API_BASE}/auth/logout`, {
                method: 'POST',
                headers: {
                    'X-Session-Token': sessionToken
                }
            });
        }
    } catch (error) {
        console.error('Logout error:', error);
    }

    // Clear local storage
    localStorage.removeItem('staffSessionToken');
    localStorage.removeItem('staffUser');

    // Reset variables
    sessionToken = null;
    currentUser = null;

    // Show login
    showLogin();

    // Reset form
    document.getElementById('loginForm').reset();
}

// Get current session token
function getSessionToken() {
    return sessionToken;
}

// Get current user
function getCurrentUser() {
    return currentUser;
}

// ==================== PERMISSIONS SYSTEM ====================

// Load full user info with permissions
async function loadUserPermissions() {
    try {
        const response = await fetch(`${API_BASE}/auth/me`, {
            headers: {
                'X-Session-Token': sessionToken
            }
        });

        if (response.ok) {
            currentUser = await response.json();
            localStorage.setItem('staffUser', JSON.stringify(currentUser));
            console.log('User permissions loaded:', currentUser.permissions);
            return currentUser;
        } else {
            console.error('Failed to load user permissions');
            return null;
        }
    } catch (error) {
        console.error('Error loading permissions:', error);
        return null;
    }
}

// Check if user has a specific permission
function hasPermission(permission) {
    if (!currentUser || !currentUser.permissions) {
        return false;
    }
    return currentUser.permissions.includes(permission);
}

// Permission constants (match backend Permission enum)
const PERMISSIONS = {
    VIEW_ALL_BOOKINGS: 'VIEW_ALL_BOOKINGS',
    VIEW_MUNICIPALITY_BOOKINGS: 'VIEW_MUNICIPALITY_BOOKINGS',
    MANAGE_BOOKINGS: 'MANAGE_BOOKINGS',
    VIEW_ALL_EMPLOYEES: 'VIEW_ALL_EMPLOYEES',
    VIEW_MUNICIPALITY_EMPLOYEES: 'VIEW_MUNICIPALITY_EMPLOYEES',
    MANAGE_EMPLOYEES: 'MANAGE_EMPLOYEES',
    VIEW_ALL_WORKLISTS: 'VIEW_ALL_WORKLISTS',
    VIEW_MUNICIPALITY_WORKLISTS: 'VIEW_MUNICIPALITY_WORKLISTS',
    MANAGE_WORKLISTS: 'MANAGE_WORKLISTS',
    ASSIGN_REQUESTS: 'ASSIGN_REQUESTS',
    VIEW_REPORTS: 'VIEW_REPORTS'
};

// Check if user can manage employees
function canManageEmployees() {
    return hasPermission(PERMISSIONS.MANAGE_EMPLOYEES);
}

// Check if user can manage worklists
function canManageWorkLists() {
    return hasPermission(PERMISSIONS.MANAGE_WORKLISTS);
}

// Check if user can assign requests
function canAssignRequests() {
    return hasPermission(PERMISSIONS.ASSIGN_REQUESTS);
}

// Check if user is ADMIN (can see all municipalities)
function isAdmin() {
    return currentUser && currentUser.role === 'ADMIN';
}

// Get user's municipality (null if ADMIN)
function getUserMunicipality() {
    return currentUser ? currentUser.municipality : null;
}
