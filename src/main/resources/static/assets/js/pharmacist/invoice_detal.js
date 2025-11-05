// Print invoice functionality
function printInvoice() {
  // Hide action buttons before printing
  const actionButtons = document.querySelector('.action-buttons');
  const backButton = document.querySelector('.back-button');

  if (actionButtons) actionButtons.style.display = 'none';
  if (backButton) backButton.style.display = 'none';

  // Print the page
  window.print();

  // Show action buttons after printing
  setTimeout(() => {
    if (actionButtons) actionButtons.style.display = 'flex';
    if (backButton) backButton.style.display = 'flex';
  }, 100);
}

// Go back functionality
function goBack() {
  // Check if there's a previous page in history
  if (window.history.length > 1) {
    window.history.back();
  } else {
    // If no history, you could redirect to a default page
    // window.location.href = '/dashboard';
    alert('Không có trang trước để quay lại');
  }
}

// Add keyboard shortcuts
document.addEventListener('keydown', function(event) {
  // Ctrl+P for print
  if (event.ctrlKey && event.key === 'p') {
    event.preventDefault();
    printInvoice();
  }

  // Escape key to go back
  if (event.key === 'Escape') {
    goBack();
  }
});

// Add print styles when printing
window.addEventListener('beforeprint', function() {
  document.body.classList.add('printing');
});

window.addEventListener('afterprint', function() {
  document.body.classList.remove('printing');
});

// Optional: Add current date functionality
function updateCurrentDate() {
  const dateElement = document.querySelector('.invoice-date');
  if (dateElement) {
    const currentDate = new Date();
    const day = currentDate.getDate();
    const month = currentDate.getMonth() + 1;
    const year = currentDate.getFullYear();

    // Uncomment the line below if you want to use current date instead of the fixed date
    // dateElement.textContent = `Ngày ${day} tháng ${month} năm ${year}`;
  }
}

// Initialize the page
document.addEventListener('DOMContentLoaded', function() {
  // You can call updateCurrentDate() here if you want to use current date
  // updateCurrentDate();

  console.log('Pharmacy invoice loaded successfully');
});
