// API_BASE is defined in config.js
let currentStep = 1;

// Load municipalities on page load
document.addEventListener('DOMContentLoaded', () => {
    loadMunicipalities();
    setMinDate();
    setupCharacterCounter();
    setupFormValidation();
});

// Set minimum date (2 days from now)
function setMinDate() {
    const dateInput = document.getElementById('collectionDate');
    const today = new Date();
    today.setDate(today.getDate() + 2);
    dateInput.min = today.toISOString().split('T')[0];

    const maxDate = new Date();
    maxDate.setDate(maxDate.getDate() + 30);
    dateInput.max = maxDate.toISOString().split('T')[0];
}

// Load municipalities from API
async function loadMunicipalities() {
    const select = document.getElementById('municipality');

    try {
        const response = await fetch(`${API_BASE}/municipalities`);
        const municipalities = await response.json();

        select.innerHTML = '<option value="">Selecione um município...</option>';
        municipalities.forEach(mun => {
            const option = document.createElement('option');
            option.value = mun.name;
            option.textContent = mun.name;
            select.appendChild(option);
        });
    } catch (error) {
        showAlert('Erro ao carregar municípios: ' + error.message, 'error');
        select.innerHTML = '<option value="">Erro ao carregar municípios</option>';
    }
}

// Character counter for description
function setupCharacterCounter() {
    const textarea = document.getElementById('itemDescription');
    const counter = document.getElementById('charCount');

    textarea.addEventListener('input', () => {
        const length = textarea.value.length;
        counter.textContent = length;

        if (length > 500) {
            counter.style.color = '#f5576c';
        } else {
            counter.style.color = '#4facfe';
        }
    });
}

// Item counter functions
function incrementItems() {
    const input = document.getElementById('numberOfItems');
    const currentValue = parseInt(input.value);
    if (currentValue < 5) {
        input.value = currentValue + 1;
    }
}

function decrementItems() {
    const input = document.getElementById('numberOfItems');
    const currentValue = parseInt(input.value);
    if (currentValue > 1) {
        input.value = currentValue - 1;
    }
}

// Step navigation
function nextStep() {
    if (!validateCurrentStep()) {
        return;
    }

    if (currentStep < 4) {
        // Mark current step as completed
        document.querySelector(`.step[data-step="${currentStep}"]`).classList.add('completed');
        document.querySelector(`.step[data-step="${currentStep}"]`).classList.remove('active');

        // Hide current form step
        document.querySelector(`.form-step[data-step="${currentStep}"]`).classList.remove('active');

        currentStep++;

        // Show next step
        document.querySelector(`.step[data-step="${currentStep}"]`).classList.add('active');
        document.querySelector(`.form-step[data-step="${currentStep}"]`).classList.add('active');

        // If last step, show summary
        if (currentStep === 4) {
            showSummary();
        }

        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}

function prevStep() {
    if (currentStep > 1) {
        // Hide current step
        document.querySelector(`.step[data-step="${currentStep}"]`).classList.remove('active');
        document.querySelector(`.form-step[data-step="${currentStep}"]`).classList.remove('active');

        currentStep--;

        // Show previous step
        document.querySelector(`.step[data-step="${currentStep}"]`).classList.add('active');
        document.querySelector(`.step[data-step="${currentStep}"]`).classList.remove('completed');
        document.querySelector(`.form-step[data-step="${currentStep}"]`).classList.add('active');

        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}

// Validate current step
function validateCurrentStep() {
    const currentFormStep = document.querySelector(`.form-step[data-step="${currentStep}"]`);
    const inputs = currentFormStep.querySelectorAll('input[required], select[required], textarea[required]');

    for (let input of inputs) {
        if (input.type === 'radio') {
            const radioGroup = currentFormStep.querySelectorAll(`input[name="${input.name}"]`);
            const isChecked = Array.from(radioGroup).some(radio => radio.checked);
            if (!isChecked) {
                showAlert('Por favor, preencha todos os campos obrigatórios', 'error');
                return false;
            }
        } else if (!input.value) {
            showAlert('Por favor, preencha todos os campos obrigatórios', 'error');
            input.focus();
            return false;
        }
    }

    return true;
}

// Show booking summary
function showSummary() {
    const municipality = document.getElementById('municipality').value;
    const address = document.getElementById('address').value;
    const itemDescription = document.getElementById('itemDescription').value;
    const numberOfItems = document.getElementById('numberOfItems').value;
    const collectionDate = document.getElementById('collectionDate').value;
    const timeSlot = document.querySelector('input[name="timeSlot"]:checked').value;
    const contactEmail = document.getElementById('contactEmail').value;
    const contactPhone = document.getElementById('contactPhone').value;

    const timeSlotText = {
        'MORNING': 'Manhã (08:00-12:00)',
        'AFTERNOON': 'Tarde (12:00-17:00)',
        'EVENING': 'Fim de Tarde (17:00-20:00)'
    };

    const summaryHTML = `
        <div class="summary-item">
            <div class="summary-label">Município:</div>
            <div class="summary-value">${municipality}</div>
        </div>
        <div class="summary-item">
            <div class="summary-label">Morada:</div>
            <div class="summary-value">${address}</div>
        </div>
        <div class="summary-item">
            <div class="summary-label">Descrição:</div>
            <div class="summary-value">${itemDescription.substring(0, 100)}${itemDescription.length > 100 ? '...' : ''}</div>
        </div>
        <div class="summary-item">
            <div class="summary-label">Número de Itens:</div>
            <div class="summary-value">${numberOfItems}</div>
        </div>
        <div class="summary-item">
            <div class="summary-label">Data:</div>
            <div class="summary-value">${formatDate(collectionDate)} - ${timeSlotText[timeSlot]}</div>
        </div>
        ${contactEmail ? `<div class="summary-item">
            <div class="summary-label">Email:</div>
            <div class="summary-value">${contactEmail}</div>
        </div>` : ''}
        ${contactPhone ? `<div class="summary-item">
            <div class="summary-label">Telefone:</div>
            <div class="summary-value">${contactPhone}</div>
        </div>` : ''}
    `;

    document.getElementById('summaryContent').innerHTML = summaryHTML;
}

// Form validation setup
function setupFormValidation() {
    const form = document.getElementById('bookingForm');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        console.log('Form submit event triggered');
        console.log('API_BASE:', API_BASE);

        const timeSlotInput = document.querySelector('input[name="timeSlot"]:checked');
        if (!timeSlotInput) {
            showAlert('Por favor, selecione um período do dia', 'error');
            return;
        }

        const formData = {
            municipality: document.getElementById('municipality').value,
            itemDescription: document.getElementById('itemDescription').value,
            numberOfItems: parseInt(document.getElementById('numberOfItems').value),
            collectionDate: document.getElementById('collectionDate').value,
            timeSlot: timeSlotInput.value,
            address: document.getElementById('address').value,
            contactEmail: document.getElementById('contactEmail').value || null,
            contactPhone: document.getElementById('contactPhone').value || null
        };

        console.log('Form data collected:', formData);

        try {
            console.log('Sending POST request to:', `${API_BASE}/bookings`);
            const response = await fetch(`${API_BASE}/bookings`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            console.log('Response status:', response.status);

            if (!response.ok) {
                const error = await response.json();
                console.error('Error response:', error);
                throw new Error(error.message || 'Erro ao criar pedido');
            }

            const booking = await response.json();
            console.log('Booking created successfully:', booking);

            showAlert(
                `<div style="text-align: center;">
                    <i class="fas fa-check-circle" style="font-size: 3em; color: #4facfe; margin-bottom: 15px; display: block;"></i>
                    <h3 style="color: white; margin-bottom: 10px;">Pedido criado com sucesso!</h3>
                    <p style="margin-bottom: 15px;">O seu pedido foi registado. Guarde o token abaixo:</p>
                    <div style="background: rgba(44, 100, 150, 0.1); padding: 15px; border-radius: 10px; margin: 15px 0;">
                        <strong style="color: #4facfe; font-size: 1.2em;">${booking.accessToken}</strong>
                    </div>
                    <small>Use este token para consultar o estado do seu pedido</small>
                </div>`,
                'success'
            );

            // Reset form and go back to step 1
            document.getElementById('bookingForm').reset();
            document.getElementById('accessToken').value = booking.accessToken;
            resetToFirstStep();

        } catch (error) {
            showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
        }
    });
}

// Reset form to first step
function resetToFirstStep() {
    // Remove all active and completed states
    document.querySelectorAll('.step').forEach(step => {
        step.classList.remove('active', 'completed');
    });
    document.querySelectorAll('.form-step').forEach(step => {
        step.classList.remove('active');
    });

    // Set first step as active
    currentStep = 1;
    document.querySelector(`.step[data-step="1"]`).classList.add('active');
    document.querySelector(`.form-step[data-step="1"]`).classList.add('active');
}

// Handle check booking form submission
document.getElementById('checkBookingForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const token = document.getElementById('accessToken').value.trim();

    if (!token) {
        showAlert('Por favor, insira um token de acesso', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/bookings/${token}`);

        if (!response.ok) {
            throw new Error('Pedido não encontrado');
        }

        const booking = await response.json();
        displayBookingDetails(booking);

    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
        document.getElementById('bookingDetails').style.display = 'none';
    }
});

// Display booking details in modal
function displayBookingDetails(booking) {
    const timeSlotText = {
        'MORNING': 'Manhã (08:00-12:00)',
        'AFTERNOON': 'Tarde (12:00-17:00)',
        'EVENING': 'Fim de Tarde (17:00-20:00)'
    };

    let html = `
        <div class="booking-info">
            <div class="info-row">
                <span class="info-label"><i class="fas fa-hashtag"></i> ID do Pedido:</span>
                <span class="info-value">#${booking.id}</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-info-circle"></i> Estado:</span>
                <span class="status-badge status-${booking.currentStatus}">${getStatusText(booking.currentStatus)}</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-city"></i> Município:</span>
                <span class="info-value">${booking.municipality}</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-calendar-alt"></i> Data de Recolha:</span>
                <span class="info-value">${formatDate(booking.collectionDate)} - ${timeSlotText[booking.timeSlot]}</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-list"></i> Itens:</span>
                <span class="info-value">${booking.itemDescription}</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-box"></i> Quantidade:</span>
                <span class="info-value">${booking.numberOfItems} item(s)</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-home"></i> Morada:</span>
                <span class="info-value">${booking.address}</span>
            </div>

            <div class="info-row">
                <span class="info-label"><i class="fas fa-clock"></i> Criado em:</span>
                <span class="info-value">${formatDateTime(booking.createdAt)}</span>
            </div>

            <h3 style="margin-top: 30px; color: #4facfe; display: flex; align-items: center; gap: 10px;">
                <i class="fas fa-history"></i> Histórico de Estados
            </h3>
            <div class="history-timeline">
    `;

    booking.statusHistory.forEach(history => {
        html += `
            <div class="history-item">
                <div class="history-status">${getStatusText(history.status)}</div>
                <div class="history-time">${formatDateTime(history.timestamp)}</div>
                ${history.notes ? `<div style="margin-top: 5px; color: rgba(255,255,255,0.7);">${history.notes}</div>` : ''}
            </div>
        `;
    });

    html += `</div>`;

    if (booking.currentStatus === 'RECEIVED' || booking.currentStatus === 'ASSIGNED') {
        html += `
            <button onclick="cancelBooking('${booking.accessToken}')" class="btn btn-danger" style="width: 100%; margin-top: 20px;">
                <i class="fas fa-times"></i> Cancelar Pedido
            </button>
        `;
    }

    html += `</div>`;

    document.getElementById('bookingDetailsContent').innerHTML = html;
    document.getElementById('bookingDetails').style.display = 'flex';
}

// Close booking details modal
function closeBookingDetails() {
    document.getElementById('bookingDetails').style.display = 'none';
}

// Cancel booking
async function cancelBooking(token) {
    if (!confirm('Tem a certeza que deseja cancelar este pedido?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/bookings/${token}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Erro ao cancelar pedido');
        }

        showAlert('<i class="fas fa-check-circle"></i> Pedido cancelado com sucesso', 'success');
        closeBookingDetails();

        // Refresh the details if needed
        setTimeout(() => {
            document.getElementById('checkBookingForm').dispatchEvent(new Event('submit'));
        }, 1000);

    } catch (error) {
        showAlert(`<i class="fas fa-exclamation-triangle"></i> ${error.message}`, 'error');
    }
}

// Utility functions
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
    }, 10000);

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
