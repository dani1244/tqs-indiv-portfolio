// API_BASE is defined in config.js
// Note: Don't load on DOMContentLoaded here, auth.js will call these functions after login

// ==================== PERMISSION-BASED UI CONTROL ====================

// Update UI based on user permissions
function updateUIBasedOnPermissions() {
    console.log('Updating UI based on permissions');

    const user = getCurrentUser();
    if (!user) return;

    console.log('Current user role:', user.role);
    console.log('Current user permissions:', user.permissions);

    // Hide "Novo Funcionário" button if user cannot manage employees
    if (!canManageEmployees()) {
        const addEmployeeBtn = document.querySelector('button[onclick="openAddEmployeeModal()"]');
        if (addEmployeeBtn) {
            addEmployeeBtn.style.display = 'none';
        }
    }

    // Hide "Nova Lista" button if user cannot manage worklists
    if (!canManageWorkLists()) {
        const addWorkListBtn = document.querySelector('button[onclick="openAddWorkListModal()"]');
        if (addWorkListBtn) {
            addWorkListBtn.style.display = 'none';
        }
    }

    // Show user info in header
    const userNameEl = document.getElementById('userName');
    const userRoleEl = document.getElementById('userRole');

    if (userNameEl) {
        userNameEl.textContent = user.fullName;
    }

    if (userRoleEl) {
        const roleText = {
            'ADMIN': 'Administrador',
            'MANAGER': 'Gestor',
            'OPERATOR': 'Operador'
        };
        userRoleEl.textContent = roleText[user.role] || user.role;

        if (user.municipality) {
            userRoleEl.textContent += ` - ${user.municipality}`;
        }
    }
}

// Load municipalities for filter
async function loadMunicipalities() {
    const select = document.getElementById('filterMunicipality');

    try {
        const response = await fetch(`${API_BASE}/municipalities`);
        const municipalities = await response.json();

        select.innerHTML = '<option value="">Todos os municípios</option>';
        municipalities.forEach(mun => {
            const option = document.createElement('option');
            option.value = mun.name;
            option.textContent = mun.name;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading municipalities:', error);
    }
}

// Load all bookings
async function loadAllBookings() {
    console.log('loadAllBookings() called');
    showLoading(true);

    try {
        console.log('Fetching bookings from:', `${API_BASE}/staff/bookings`);
        const response = await fetch(`${API_BASE}/staff/bookings`);
        console.log('Response status:', response.status);

        const bookings = await response.json();
        console.log('Bookings loaded:', bookings.length, bookings);

        updateStatistics(bookings);
        displayBookings(bookings);
    } catch (error) {
        console.error('Error loading bookings:', error);
        showAlert(`<i class="fas fa-exclamation-triangle"></i> Erro ao carregar pedidos: ${error.message}`, 'error');
        displayBookings([]);
    } finally {
        showLoading(false);
    }
}

// Update statistics cards
function updateStatistics(bookings) {
    const stats = {
        RECEIVED: 0,
        ASSIGNED: 0,
        IN_PROGRESS: 0,
        COMPLETED: 0
    };

    bookings.forEach(booking => {
        if (stats.hasOwnProperty(booking.currentStatus)) {
            stats[booking.currentStatus]++;
        }
    });

    // Animate counters
    animateValue('statReceived', 0, stats.RECEIVED, 1000);
    animateValue('statAssigned', 0, stats.ASSIGNED, 1000);
    animateValue('statInProgress', 0, stats.IN_PROGRESS, 1000);
    animateValue('statCompleted', 0, stats.COMPLETED, 1000);
}

// Animate counter
function animateValue(elementId, start, end, duration) {
    const element = document.getElementById(elementId);
    const range = end - start;
    const increment = range / (duration / 16);
    let current = start;

    const timer = setInterval(() => {
        current += increment;
        if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
            element.textContent = end;
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(current);
        }
    }, 16);
}

// Apply filters
async function applyFilters() {
    showLoading(true);

    const municipality = document.getElementById('filterMunicipality').value;
    const status = document.getElementById('filterStatus').value;

    let url = `${API_BASE}/staff/bookings`;
    const params = new URLSearchParams();

    if (municipality) params.append('municipality', municipality);
    if (status) params.append('status', status);

    if (params.toString()) {
        url += '?' + params.toString();
    }

    try {
        const response = await fetch(url);
        const bookings = await response.json();

        displayBookings(bookings);
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> Erro ao filtrar pedidos: ${error.message}`, 'error');
        displayBookings([]);
    } finally {
        showLoading(false);
    }
}

// Clear filters
function clearFilters() {
    document.getElementById('filterMunicipality').value = '';
    document.getElementById('filterStatus').value = '';
    loadAllBookings();
}

// Display bookings in table
function displayBookings(bookings) {
    console.log('displayBookings() called with', bookings.length, 'bookings');
    const container = document.getElementById('bookingsList');
    console.log('Container element:', container);

    if (bookings.length === 0) {
        console.log('No bookings to display');
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-inbox"></i>
                <h3>Nenhum pedido encontrado</h3>
                <p>Não existem pedidos que correspondam aos critérios selecionados</p>
            </div>
        `;
        return;
    }

    const timeSlotText = {
        'MORNING': 'Manhã',
        'AFTERNOON': 'Tarde',
        'EVENING': 'Fim de Tarde'
    };

    let html = `
        <table class="bookings-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Município</th>
                    <th>Data</th>
                    <th>Período</th>
                    <th>Itens</th>
                    <th>Estado</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
    `;

    bookings.forEach(booking => {
        html += `
            <tr>
                <td><span class="booking-id">#${booking.id}</span></td>
                <td>${booking.municipality}</td>
                <td>${formatDate(booking.collectionDate)}</td>
                <td>${timeSlotText[booking.timeSlot]}</td>
                <td>${booking.numberOfItems}</td>
                <td><span class="status-badge status-${booking.currentStatus}">${getStatusText(booking.currentStatus)}</span></td>
                <td>
                    <div class="booking-actions">
                        <button onclick="viewDetails(${booking.id})" class="btn-sm btn-view">
                            <i class="fas fa-eye"></i> Ver
                        </button>
                        ${booking.currentStatus !== 'COMPLETED' && booking.currentStatus !== 'CANCELLED' ?
                            `<button onclick="openUpdateModal(${booking.id}, '${booking.currentStatus}')" class="btn-sm btn-edit">
                                <i class="fas fa-edit"></i> Editar
                            </button>` : ''
                        }
                    </div>
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

// View booking details
async function viewDetails(bookingId) {
    try {
        const response = await fetch(`${API_BASE}/staff/bookings`);
        const bookings = await response.json();
        const booking = bookings.find(b => b.id === bookingId);

        if (!booking) {
            throw new Error('Pedido não encontrado');
        }

        const timeSlotText = {
            'MORNING': 'Manhã (08:00-12:00)',
            'AFTERNOON': 'Tarde (12:00-17:00)',
            'EVENING': 'Fim de Tarde (17:00-20:00)'
        };

        let detailsHtml = `
            <div class="details-grid">
                <div class="detail-item">
                    <div class="detail-label">Token de Acesso</div>
                    <div class="detail-value"><code>${booking.accessToken}</code></div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Estado Atual</div>
                    <div class="detail-value">
                        <span class="status-badge status-${booking.currentStatus}">${getStatusText(booking.currentStatus)}</span>
                    </div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Município</div>
                    <div class="detail-value">${booking.municipality}</div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Data de Recolha</div>
                    <div class="detail-value">${formatDate(booking.collectionDate)}</div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Período</div>
                    <div class="detail-value">${timeSlotText[booking.timeSlot]}</div>
                </div>

                <div class="detail-item">
                    <div class="detail-label">Número de Itens</div>
                    <div class="detail-value">${booking.numberOfItems} item(s)</div>
                </div>
            </div>

            <div class="detail-item" style="margin-bottom: 20px;">
                <div class="detail-label">Descrição dos Itens</div>
                <div class="detail-value">${booking.itemDescription}</div>
            </div>

            <div class="detail-item" style="margin-bottom: 20px;">
                <div class="detail-label">Morada</div>
                <div class="detail-value">${booking.address}</div>
            </div>

            ${booking.contactEmail || booking.contactPhone ? `
                <div class="details-grid">
                    ${booking.contactEmail ? `
                        <div class="detail-item">
                            <div class="detail-label">Email</div>
                            <div class="detail-value">${booking.contactEmail}</div>
                        </div>
                    ` : ''}
                    ${booking.contactPhone ? `
                        <div class="detail-item">
                            <div class="detail-label">Telefone</div>
                            <div class="detail-value">${booking.contactPhone}</div>
                        </div>
                    ` : ''}
                </div>
            ` : ''}

            <div class="details-grid">
                <div class="detail-item">
                    <div class="detail-label">Criado em</div>
                    <div class="detail-value">${formatDateTime(booking.createdAt)}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Última Atualização</div>
                    <div class="detail-value">${formatDateTime(booking.updatedAt)}</div>
                </div>
            </div>

            <h3 style="margin-top: 30px; color: #4facfe; display: flex; align-items: center; gap: 10px;">
                <i class="fas fa-history"></i> Histórico de Estados
            </h3>
            <div class="history-timeline">
        `;

        booking.statusHistory.forEach(history => {
            detailsHtml += `
                <div class="history-item">
                    <div class="history-status">${getStatusText(history.status)}</div>
                    <div class="history-time">${formatDateTime(history.timestamp)}</div>
                    ${history.notes ? `<div style="margin-top: 5px; color: rgba(255,255,255,0.7);">${history.notes}</div>` : ''}
                </div>
            `;
        });

        detailsHtml += `</div>`;

        document.getElementById('detailsContent').innerHTML = detailsHtml;
        document.getElementById('detailsModal').style.display = 'flex';

    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

// Close details modal
function closeDetailsModal() {
    document.getElementById('detailsModal').style.display = 'none';
}

// Open update status modal
function openUpdateModal(bookingId, currentStatus) {
    document.getElementById('updateBookingId').value = bookingId;

    // Set available status options based on current status
    const statusSelect = document.getElementById('newStatus');
    statusSelect.innerHTML = '<option value="">Selecione o novo estado...</option>';

    const allowedTransitions = {
        'RECEIVED': ['ASSIGNED', 'CANCELLED'],
        'ASSIGNED': ['IN_PROGRESS', 'CANCELLED'],
        'IN_PROGRESS': ['COMPLETED', 'CANCELLED']
    };

    const allowed = allowedTransitions[currentStatus] || [];
    allowed.forEach(status => {
        const option = document.createElement('option');
        option.value = status;
        option.textContent = getStatusText(status);
        statusSelect.appendChild(option);
    });

    document.getElementById('updateModal').style.display = 'flex';
}

// Close update modal
function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
    document.getElementById('updateStatusForm').reset();
}

// Handle update status form submission
document.getElementById('updateStatusForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const bookingId = document.getElementById('updateBookingId').value;
    const newStatus = document.getElementById('newStatus').value;
    const notes = document.getElementById('statusNotes').value;

    try {
        const response = await fetch(`${API_BASE}/staff/bookings/${bookingId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                status: newStatus,
                notes: notes || null
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao atualizar estado');
        }

        showAlert('<i class="fas fa-check-circle"></i> Estado atualizado com sucesso!', 'success');
        closeUpdateModal();
        loadAllBookings();

    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
});

// Utility functions
function showLoading(show) {
    document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
    document.getElementById('bookingsList').style.display = show ? 'none' : 'block';
}

function showAlert(message, type) {
    const container = document.getElementById('alertContainer');
    const alertClass = type === 'success' ? 'alert-success' : 'alert-error';

    container.innerHTML = `
        <div class="alert ${alertClass}" style="animation: slideDown 0.3s;">
            ${message}
        </div>
    `;

    setTimeout(() => {
        container.innerHTML = '';
    }, 5000);

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-PT');
}

function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('pt-PT');
}

function getStatusText(status) {
    const statusTexts = {
        'RECEIVED': 'Recebido',
        'ASSIGNED': 'Atribuído',
        'IN_PROGRESS': 'Em Progresso',
        'COMPLETED': 'Concluído',
        'CANCELLED': 'Cancelado'
    };
    return statusTexts[status] || status;
}

// ==================== TAB MANAGEMENT ====================

function switchTab(tabName, event) {
    // Remove active class from all tabs
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    // Add active class to selected tab
    if (event && event.target) {
        event.target.closest('.tab-btn').classList.add('active');
    }
    document.getElementById(`${tabName}Tab`).classList.add('active');

    // Load data for the tab
    if (tabName === 'employees') {
        loadAllEmployees();
    } else if (tabName === 'worklists') {
        loadAllWorkLists();
    }
}

// ==================== EMPLOYEES MANAGEMENT ====================

async function loadAllEmployees() {
    try {
        const response = await fetch(`${API_BASE}/employees`);
        const employees = await response.json();

        // Load municipalities for filter
        const munResponse = await fetch(`${API_BASE}/municipalities`);
        const municipalities = await munResponse.json();

        const select = document.getElementById('filterEmployeeMunicipality');
        select.innerHTML = '<option value="">Todos os municípios</option>';
        municipalities.forEach(mun => {
            const option = document.createElement('option');
            option.value = mun.name;
            option.textContent = mun.name;
            select.appendChild(option);
        });

        displayEmployees(employees);
    } catch (error) {
        console.error('Error loading employees:', error);
        showAlert(`<i class="fas fa-exclamation-triangle"></i> Erro ao carregar funcionários: ${error.message}`, 'error');
    }
}

function filterEmployees() {
    const municipality = document.getElementById('filterEmployeeMunicipality').value;
    const role = document.getElementById('filterEmployeeRole').value;

    let url = `${API_BASE}/employees`;
    const params = new URLSearchParams();

    if (municipality) params.append('municipality', municipality);
    if (role) params.append('role', role);

    if (params.toString()) {
        url += '?' + params.toString();
    }

    fetch(url)
        .then(response => response.json())
        .then(employees => displayEmployees(employees))
        .catch(error => {
            console.error('Error filtering employees:', error);
            showAlert(`<i class="fas fa-exclamation-triangle"></i> Erro ao filtrar funcionários`, 'error');
        });
}

function displayEmployees(employees) {
    const container = document.getElementById('employeesList');

    if (employees.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-users-slash"></i>
                <h3>Nenhum funcionário encontrado</h3>
                <p>Não existem funcionários que correspondam aos critérios selecionados</p>
            </div>
        `;
        return;
    }

    const roleText = {
        'DRIVER': 'Motorista',
        'COLLECTOR': 'Coletor',
        'SUPERVISOR': 'Supervisor',
        'COORDINATOR': 'Coordenador'
    };

    let html = `
        <table class="bookings-table">
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Telefone</th>
                    <th>Função</th>
                    <th>Município</th>
                    <th>Estado</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
    `;

    employees.forEach(employee => {
        html += `
            <tr>
                <td><strong>${employee.name}</strong></td>
                <td>${employee.email}</td>
                <td>${employee.phone || 'N/A'}</td>
                <td><span class="status-badge status-ASSIGNED">${roleText[employee.role]}</span></td>
                <td>${employee.municipality}</td>
                <td>
                    ${employee.active ?
                        '<span class="status-badge status-COMPLETED">Ativo</span>' :
                        '<span class="status-badge status-CANCELLED">Inativo</span>'}
                </td>
                <td>
                    <div class="booking-actions">
                        ${canManageEmployees() ? (
                            employee.active ?
                                `<button onclick="deactivateEmployee(${employee.id})" class="btn-sm" style="background: rgba(245, 87, 108, 0.2); color: #f5576c;">
                                    <i class="fas fa-user-slash"></i> Desativar
                                </button>` :
                                `<button onclick="activateEmployee(${employee.id})" class="btn-sm" style="background: rgba(85, 239, 196, 0.2); color: #55efc4;">
                                    <i class="fas fa-user-check"></i> Ativar
                                </button>`
                        ) : '<span style="color: rgba(255,255,255,0.5);">Sem permissão</span>'}
                    </div>
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

async function deactivateEmployee(employeeId) {
    if (!confirm('Tem certeza que deseja desativar este funcionário?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/employees/${employeeId}/deactivate`, {
            method: 'PATCH'
        });

        if (response.ok) {
            showAlert('<i class="fas fa-check-circle"></i> Funcionário desativado com sucesso!', 'success');
            loadAllEmployees();
        } else {
            throw new Error('Erro ao desativar funcionário');
        }
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

async function activateEmployee(employeeId) {
    try {
        const response = await fetch(`${API_BASE}/employees/${employeeId}/activate`, {
            method: 'PATCH'
        });

        if (response.ok) {
            showAlert('<i class="fas fa-check-circle"></i> Funcionário ativado com sucesso!', 'success');
            loadAllEmployees();
        } else {
            throw new Error('Erro ao ativar funcionário');
        }
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

async function openAddEmployeeModal() {
    // Load municipalities
    try {
        const response = await fetch(`${API_BASE}/municipalities`);
        const municipalities = await response.json();

        const select = document.getElementById('employeeMunicipality');
        select.innerHTML = '<option value="">Selecione o município</option>';
        municipalities.forEach(mun => {
            const option = document.createElement('option');
            option.value = mun.name;
            option.textContent = mun.name;
            select.appendChild(option);
        });

        document.getElementById('addEmployeeModal').style.display = 'flex';
    } catch (error) {
        showAlert('<i class="fas fa-exclamation-triangle"></i> Erro ao carregar municípios', 'error');
    }
}

function closeAddEmployeeModal() {
    document.getElementById('addEmployeeModal').style.display = 'none';
    document.getElementById('addEmployeeForm').reset();
}

async function submitNewEmployee(event) {
    event.preventDefault();

    const employeeData = {
        name: document.getElementById('employeeName').value,
        email: document.getElementById('employeeEmail').value,
        phone: document.getElementById('employeePhone').value,
        role: document.getElementById('employeeRole').value,
        municipality: document.getElementById('employeeMunicipality').value
    };

    try {
        const response = await fetch(`${API_BASE}/employees`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(employeeData)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao criar funcionário');
        }

        const employee = await response.json();
        showAlert(`<i class="fas fa-check-circle"></i> Funcionário ${employee.name} criado com sucesso!`, 'success');
        closeAddEmployeeModal();
        loadAllEmployees();
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

// ==================== WORKLISTS MANAGEMENT ====================

async function loadAllWorkLists() {
    try {
        const response = await fetch(`${API_BASE}/worklists`);
        const workLists = await response.json();

        // Load municipalities for filter
        const munResponse = await fetch(`${API_BASE}/municipalities`);
        const municipalities = await munResponse.json();

        const select = document.getElementById('filterWorkListMunicipality');
        select.innerHTML = '<option value="">Todos os municípios</option>';
        municipalities.forEach(mun => {
            const option = document.createElement('option');
            option.value = mun.name;
            option.textContent = mun.name;
            select.appendChild(option);
        });

        // Set today's date as default
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('filterWorkListDate').value = today;

        displayWorkLists(workLists);
    } catch (error) {
        console.error('Error loading work lists:', error);
        showAlert(`<i class="fas fa-exclamation-triangle"></i> Erro ao carregar listas de trabalho: ${error.message}`, 'error');
    }
}

function filterWorkLists() {
    const date = document.getElementById('filterWorkListDate').value;
    const municipality = document.getElementById('filterWorkListMunicipality').value;

    let url = `${API_BASE}/worklists`;
    const params = new URLSearchParams();

    if (date) params.append('date', date);
    if (municipality) params.append('municipality', municipality);

    if (params.toString()) {
        url += '?' + params.toString();
    }

    fetch(url)
        .then(response => response.json())
        .then(workLists => displayWorkLists(workLists))
        .catch(error => {
            console.error('Error filtering work lists:', error);
            showAlert(`<i class="fas fa-exclamation-triangle"></i> Erro ao filtrar listas`, 'error');
        });
}

function displayWorkLists(workLists) {
    const container = document.getElementById('workListsList');

    if (workLists.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-clipboard"></i>
                <h3>Nenhuma lista de trabalho encontrada</h3>
                <p>Não existem listas para os critérios selecionados</p>
            </div>
        `;
        return;
    }

    let html = `
        <table class="bookings-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Data</th>
                    <th>Funcionário</th>
                    <th>Município</th>
                    <th>Pedidos</th>
                    <th>Itens</th>
                    <th>Estado</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
    `;

    workLists.forEach(workList => {
        const statusClass = {
            'PENDING': 'RECEIVED',
            'IN_PROGRESS': 'IN_PROGRESS',
            'COMPLETED': 'COMPLETED',
            'CANCELLED': 'CANCELLED'
        };

        html += `
            <tr>
                <td><span class="booking-id">#${workList.id}</span></td>
                <td>${formatDate(workList.workDate)}</td>
                <td>${workList.employeeName || 'N/A'}</td>
                <td>${workList.municipality}</td>
                <td>${workList.totalRequests || 0}</td>
                <td>${workList.totalItems || 0}</td>
                <td><span class="status-badge status-${statusClass[workList.status]}">${getWorkListStatusText(workList.status)}</span></td>
                <td>
                    <div class="booking-actions">
                        ${canAssignRequests() && (workList.status === 'PENDING' || workList.status === 'IN_PROGRESS') ?
                            `<button onclick="openAssignRequestsModal(${workList.id})" class="btn-sm" style="background: rgba(79, 172, 254, 0.2); color: #4facfe;">
                                <i class="fas fa-tasks"></i> Atribuir
                            </button>` : ''}
                        ${canManageWorkLists() && workList.status === 'PENDING' ?
                            `<button onclick="startWorkList(${workList.id})" class="btn-sm btn-edit">
                                <i class="fas fa-play"></i> Iniciar
                            </button>` : ''}
                        ${canManageWorkLists() && workList.status === 'IN_PROGRESS' ?
                            `<button onclick="completeWorkList(${workList.id})" class="btn-sm" style="background: rgba(85, 239, 196, 0.2); color: #55efc4;">
                                <i class="fas fa-check"></i> Concluir
                            </button>` : ''}
                    </div>
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

function getWorkListStatusText(status) {
    const statusTexts = {
        'PENDING': 'Pendente',
        'IN_PROGRESS': 'Em Progresso',
        'COMPLETED': 'Concluída',
        'CANCELLED': 'Cancelada'
    };
    return statusTexts[status] || status;
}

async function startWorkList(workListId) {
    try {
        const response = await fetch(`${API_BASE}/worklists/${workListId}/start`, {
            method: 'PATCH'
        });

        if (response.ok) {
            showAlert('<i class="fas fa-check-circle"></i> Lista de trabalho iniciada!', 'success');
            loadAllWorkLists();
        } else {
            throw new Error('Erro ao iniciar lista');
        }
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

async function completeWorkList(workListId) {
    try {
        const response = await fetch(`${API_BASE}/worklists/${workListId}/complete`, {
            method: 'PATCH'
        });

        if (response.ok) {
            showAlert('<i class="fas fa-check-circle"></i> Lista de trabalho concluída!', 'success');
            loadAllWorkLists();
        } else {
            throw new Error('Erro ao concluir lista');
        }
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

async function openAddWorkListModal() {
    // Load municipalities
    try {
        const response = await fetch(`${API_BASE}/municipalities`);
        const municipalities = await response.json();

        const select = document.getElementById('workListMunicipality');
        select.innerHTML = '<option value="">Selecione o município</option>';
        municipalities.forEach(mun => {
            const option = document.createElement('option');
            option.value = mun.name;
            option.textContent = mun.name;
            select.appendChild(option);
        });

        // Set minimum date to tomorrow
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        document.getElementById('workListDate').min = tomorrow.toISOString().split('T')[0];

        document.getElementById('addWorkListModal').style.display = 'flex';
    } catch (error) {
        showAlert('<i class="fas fa-exclamation-triangle"></i> Erro ao carregar municípios', 'error');
    }
}

function closeAddWorkListModal() {
    document.getElementById('addWorkListModal').style.display = 'none';
    document.getElementById('addWorkListForm').reset();
}

async function loadEmployeesForWorkList() {
    const municipality = document.getElementById('workListMunicipality').value;
    const employeeSelect = document.getElementById('workListEmployee');

    if (!municipality) {
        employeeSelect.innerHTML = '<option value="">Selecione primeiro o município</option>';
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/employees?municipality=${municipality}`);
        const employees = await response.json();

        // Filter active employees only
        const activeEmployees = employees.filter(emp => emp.active);

        employeeSelect.innerHTML = '<option value="">Selecione o funcionário</option>';

        if (activeEmployees.length === 0) {
            employeeSelect.innerHTML = '<option value="">Nenhum funcionário ativo neste município</option>';
            return;
        }

        const roleText = {
            'DRIVER': 'Motorista',
            'COLLECTOR': 'Coletor',
            'SUPERVISOR': 'Supervisor',
            'COORDINATOR': 'Coordenador'
        };

        activeEmployees.forEach(emp => {
            const option = document.createElement('option');
            option.value = emp.id;
            option.textContent = `${emp.name} - ${roleText[emp.role]}`;
            employeeSelect.appendChild(option);
        });
    } catch (error) {
        showAlert('<i class="fas fa-exclamation-triangle"></i> Erro ao carregar funcionários', 'error');
    }
}

async function submitNewWorkList(event) {
    event.preventDefault();

    const workListData = {
        workDate: document.getElementById('workListDate').value,
        municipality: document.getElementById('workListMunicipality').value,
        employeeId: parseInt(document.getElementById('workListEmployee').value),
        notes: document.getElementById('workListNotes').value || null
    };

    try {
        const response = await fetch(`${API_BASE}/worklists`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(workListData)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao criar lista de trabalho');
        }

        const workList = await response.json();
        showAlert(`<i class="fas fa-check-circle"></i> Lista de trabalho #${workList.id} criada com sucesso!`, 'success');
        closeAddWorkListModal();
        loadAllWorkLists();
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

// ==================== ASSIGN REQUESTS TO WORKLIST ====================

let currentWorkListForAssignment = null;

async function openAssignRequestsModal(workListId) {
    currentWorkListForAssignment = workListId;

    try {
        // Load WorkList details
        const wlResponse = await fetch(`${API_BASE}/worklists/${workListId}`);
        const workList = await wlResponse.json();

        // Display WorkList info
        const roleText = {
            'DRIVER': 'Motorista',
            'COLLECTOR': 'Coletor',
            'SUPERVISOR': 'Supervisor',
            'COORDINATOR': 'Coordenador'
        };

        document.getElementById('workListInfo').innerHTML = `
            <h3><i class="fas fa-clipboard-check"></i> Lista #${workList.id}</h3>
            <p><strong>Data:</strong> ${formatDate(workList.workDate)}</p>
            <p><strong>Funcionário:</strong> ${workList.employeeName || 'N/A'}</p>
            <p><strong>Município:</strong> ${workList.municipality}</p>
            <p><strong>Pedidos atribuídos:</strong> ${workList.totalRequests || 0}</p>
        `;

        // Load available requests (RECEIVED status from same municipality)
        const reqResponse = await fetch(`${API_BASE}/staff/bookings`);
        const allBookings = await reqResponse.json();

        // Filter: same municipality, RECEIVED status
        const availableRequests = allBookings.filter(booking =>
            booking.municipality === workList.municipality &&
            booking.currentStatus === 'RECEIVED'
        );

        displayAvailableRequests(availableRequests);

        document.getElementById('assignRequestsModal').style.display = 'flex';
    } catch (error) {
        showAlert('<i class="fas fa-exclamation-triangle"></i> Erro ao carregar dados', 'error');
    }
}

function closeAssignRequestsModal() {
    document.getElementById('assignRequestsModal').style.display = 'none';
    currentWorkListForAssignment = null;
}

function displayAvailableRequests(requests) {
    const container = document.getElementById('availableRequestsList');

    if (requests.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-inbox"></i>
                <h3>Nenhum pedido disponível</h3>
                <p>Não existem pedidos recebidos para atribuir a esta lista</p>
            </div>
        `;
        return;
    }

    let html = `
        <table class="bookings-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Data Recolha</th>
                    <th>Morada</th>
                    <th>Itens</th>
                    <th>Ação</th>
                </tr>
            </thead>
            <tbody>
    `;

    requests.forEach(request => {
        html += `
            <tr>
                <td><span class="booking-id">#${request.id}</span></td>
                <td>${formatDate(request.collectionDate)} - ${getTimeSlotText(request.timeSlot)}</td>
                <td>${request.address}</td>
                <td>${request.numberOfItems}</td>
                <td>
                    <button onclick="assignRequestToWorkList(${request.id})" class="btn-sm btn-edit">
                        <i class="fas fa-plus"></i> Atribuir
                    </button>
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

function getTimeSlotText(timeSlot) {
    const slots = {
        'MORNING': 'Manhã',
        'AFTERNOON': 'Tarde',
        'EVENING': 'Fim de Tarde'
    };
    return slots[timeSlot] || timeSlot;
}

async function assignRequestToWorkList(requestId) {
    if (!currentWorkListForAssignment) {
        showAlert('<i class="fas fa-exclamation-triangle"></i> Erro: Lista de trabalho não identificada', 'error');
        return;
    }

    try {
        const response = await fetch(
            `${API_BASE}/worklists/${currentWorkListForAssignment}/requests/${requestId}`,
            { method: 'POST' }
        );

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao atribuir pedido');
        }

        showAlert('<i class="fas fa-check-circle"></i> Pedido atribuído com sucesso!', 'success');

        // Refresh the modal with updated data
        openAssignRequestsModal(currentWorkListForAssignment);
        loadAllWorkLists();
    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}
