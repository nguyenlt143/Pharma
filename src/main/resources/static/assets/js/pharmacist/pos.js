
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

// Search functionality
if (searchButton && searchInput) {
  searchButton.addEventListener('click', () => {
    const searchTerm = searchInput.value.trim();
    if (searchTerm) searchMedication(searchTerm);
  });

  searchInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') searchButton.click();
  });
}

// Clear buttons
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
if (paymentButton) {
  paymentButton.addEventListener('click', () => {
    const customerName = customerNameInput.value.trim();
    const phoneNumber = phoneInput.value.trim();
    const paymentAmount = paymentAmountInput.value.trim();
    const paymentMethod = paymentMethodSelect.value;
    const notes = notesTextarea.value.trim();

    if (!paymentAmount) {
      alert('Vui lòng nhập số tiền thanh toán');
      paymentAmountInput.focus();
      return;
    }

    processPayment({
      customerName,
      phoneNumber,
      paymentAmount: parseFloat(paymentAmount),
      paymentMethod,
      notes
    });
  });
}

// F8 focus payment input
document.addEventListener('keydown', (e) => {
  if (e.key === 'F8') {
    e.preventDefault();
    paymentAmountInput.focus();
  }
});

// Auto-calc change
if (paymentAmountInput) {
  paymentAmountInput.addEventListener('input', () => {
    const paymentAmount = parseFloat(paymentAmountInput.value) || 0;
    const totalAmount = 0; // TODO: replace with actual total
    const change = Math.max(0, paymentAmount - totalAmount);

    const changeElement = document.querySelector('.payment-row:last-of-type .payment-value');
    if (changeElement) {
      changeElement.textContent = change.toFixed(2);
    }
  });
}

// Utility functions
function searchMedication(searchTerm) {
  console.log('Searching for medication:', searchTerm);
  setTimeout(() => {
    console.log('Search results for:', searchTerm);
  }, 500);
}

function processPayment(paymentData) {
  console.log('Processing payment:', paymentData);
  setTimeout(() => {
    alert('Thanh toán thành công!');
    clearPaymentForm();
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
  const totalAmountElements = document.querySelectorAll('.total-amount, .payment-value');
  totalAmountElements.forEach(element => {
    if (element.textContent === '') {
      element.textContent = '0.00';
    }
  });
}

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
  if (e.ctrlKey && e.key === 'f') {
    e.preventDefault();
    if (searchInput) {
      searchInput.focus();
      searchInput.select();
    }
  }

  if (e.key === 'Escape' && document.activeElement === searchInput) {
    searchInput.value = '';
  }
});

// Initialize POS page
document.addEventListener('DOMContentLoaded', () => {
  updatePaymentTotals();
  if (searchInput) searchInput.focus();
});
