const API_BASE = 'http://localhost:8080/api';

// Load statistics on page load
document.addEventListener('DOMContentLoaded', () => {
    loadStatistics();
    animateNumbers();
});

// Load real statistics from API
async function loadStatistics() {
    try {
        // Load booking count
        const bookingsResponse = await fetch(`${API_BASE}/staff/bookings`);
        if (bookingsResponse.ok) {
            const bookings = await bookingsResponse.json();
            animateCounter('bookingCount', bookings.length);
        }
    } catch (error) {
        console.log('Could not load statistics:', error);
    }
}

// Animate counter numbers
function animateCounter(elementId, targetValue) {
    const element = document.getElementById(elementId);
    const duration = 2000;
    const steps = 60;
    const stepValue = targetValue / steps;
    const stepDuration = duration / steps;

    let currentValue = 0;
    const timer = setInterval(() => {
        currentValue += stepValue;
        if (currentValue >= targetValue) {
            element.textContent = targetValue;
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(currentValue);
        }
    }, stepDuration);
}

// Animate stat numbers on scroll
function animateNumbers() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate');
            }
        });
    }, { threshold: 0.5 });

    document.querySelectorAll('.stat-item').forEach(item => {
        observer.observe(item);
    });
}
