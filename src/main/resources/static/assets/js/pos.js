// DOM Elements
const searchInput = document.querySelector('.search-input');
const searchButton = document.querySelector('.search-button');
const clearButtons = document.querySelectorAll('.clear-button');
const paymentButton = document.querySelector('.payment-button');
const customerNameInput = document.querySelector('.customer-info .form-input');
const phoneInput = document.querySelector('.customer-info .form-group:nth-child(2) .form-input');
const paymentAmountInput = document.querySelector('.payment-details .form-input');
const paymentMethodSelect = document.querySelector('.form-select');
const notesTextarea = document.querySelector('.form-textarea');

// Navigation functionality
const navLinks = document.querySelectorAll('.nav-link');
navLinks.forEach(link => {
  link.addEventListener('click', (e) => {
    e.preventDefault();

    // Remove active class from all links
    navLinks.forEach(l => l.classList.remove('active'));

    // Add active class to clicked link
    link.classList.add('active');

    console.log('Navigation clicked:', link.querySelector('.nav-text').textContent);
  });
});

// Search functionality
searchButton.addEventListener('click', () => {
  const searchTerm = searchInput.value.trim();
  if (searchTerm) {
    console.log('Searching for:', searchTerm);
    // Here you would typically make an API call to search for medications
    searchMedication(searchTerm);
  }
});

searchInput.addEventListener('keypress', (e) => {
  if (e.key === 'Enter') {
    searchButton.click();
  }
});

// Clear button functionality
clearButtons.forEach(button => {
  button.addEventListener('click', () => {
    const input = button.parentElement.querySelector('.form-input');
    if (input) {
      input.value = '';
      input.focus();
    }
  });
});

// Payment functionality
paymentButton.addEventListener('click', () => {
  const customerName = customerNameInput.value.trim();
  const phoneNumber = phoneInput.value.trim();
  const paymentAmount = paymentAmountInput.value.trim();
  const paymentMethod = paymentMethodSelect.value;
  const notes = notesTextarea.value.trim();

  // Basic validation
  if (!paymentAmount) {
    alert('Vui lòng nhập số tiền thanh toán');
    paymentAmountInput.focus();
    return;
  }

  // Process payment
  processPayment({
    customerName,
    phoneNumber,
    paymentAmount: parseFloat(paymentAmount),
    paymentMethod,
    notes
  });
});

// F8 key functionality for payment input
document.addEventListener('keydown', (e) => {
  if (e.key === 'F8') {
    e.preventDefault();
    paymentAmountInput.focus();
  }
});

// Menu button functionality
const menuButton = document.querySelector('.menu-button');
const sidebar = document.querySelector('.sidebar');

menuButton.addEventListener('click', () => {
  sidebar.classList.toggle('collapsed');
});

// Back button functionality
const backButton = document.querySelector('.back-button');
backButton.addEventListener('click', () => {
  console.log('Back button clicked');
  // Here you would typically navigate back or show a confirmation dialog
});

// Dropdown button functionality
const dropdownButtons = document.querySelectorAll('.dropdown-button, .user-dropdown');
dropdownButtons.forEach(button => {
  button.addEventListener('click', () => {
    console.log('Dropdown clicked');
    // Here you would typically show a dropdown menu
  });
});

// Utility functions
function searchMedication(searchTerm) {
  // Simulate API call
  console.log('Searching for medication:', searchTerm);

  // Here you would make an actual API call
  // For now, we'll just simulate a search
  setTimeout(() => {
    console.log('Search results for:', searchTerm);
    // Update the UI with search results
  }, 500);
}

function processPayment(paymentData) {
  console.log('Processing payment:', paymentData);

  // Simulate payment processing
  setTimeout(() => {
    alert('Thanh toán thành công!');

    // Clear form after successful payment
    clearPaymentForm();

    // Update totals
    updatePaymentTotals();
  }, 1000);
}

function clearPaymentForm() {
  customerNameInput.value = '';
  phoneInput.value = '';
  paymentAmountInput.value = '';
  notesTextarea.value = '';
  paymentMethodSelect.selectedIndex = 0;
}

function updatePaymentTotals() {
  // Update the payment totals in the UI
  const totalAmountElements = document.querySelectorAll('.total-amount, .payment-value');
  totalAmountElements.forEach(element => {
    if (element.textContent === '') {
      element.textContent = '0.00';
    }
  });
}

// Auto-calculate change
paymentAmountInput.addEventListener('input', () => {
  const paymentAmount = parseFloat(paymentAmountInput.value) || 0;
  const totalAmount = 0; // This would come from the prescription total
  const change = Math.max(0, paymentAmount - totalAmount);

  // Update change display
  const changeElement = document.querySelector('.payment-row:last-of-type .payment-value');
  if (changeElement) {
    changeElement.textContent = change.toFixed(2);
  }
});

// Initialize the application
function initializeApp() {
  console.log('Pharmacy Management System initialized');

  // Set current date
  const dateElement = document.querySelector('.date-text');
  if (dateElement) {
    const currentDate = new Date();
    const formattedDate = currentDate.toLocaleDateString('vi-VN');
    dateElement.textContent = formattedDate;
  }

  // Initialize payment totals
  updatePaymentTotals();

  // Focus on search input
  if (searchInput) {
    searchInput.focus();
  }
}

// Start the application when DOM is loaded
document.addEventListener('DOMContentLoaded', initializeApp);

// Handle window resize
window.addEventListener('resize', () => {
  // Adjust layout if needed
  console.log('Window resized');
});

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
  // Ctrl+F for search
  if (e.ctrlKey && e.key === 'f') {
    e.preventDefault();
    searchInput.focus();
    searchInput.select();
  }

  // Escape to clear search
  if (e.key === 'Escape') {
    if (document.activeElement === searchInput) {
      searchInput.value = '';
    }
  }
});
