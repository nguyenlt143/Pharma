
  // Date input functionality
  const dateInput = document.querySelector('.date-input');
  if (dateInput) {
    dateInput.addEventListener('click', function() {
      // You can integrate a date picker library here
      console.log('Date input clicked');
    });
  }

  // Table row hover effects
  const tableRows = document.querySelectorAll('.table-row:not(.total-row)');
  tableRows.forEach(row => {
    row.addEventListener('mouseenter', function() {
      this.style.backgroundColor = '#f8fafc';
    });

    row.addEventListener('mouseleave', function() {
      this.style.backgroundColor = '#ffffff';
    });
  });

  // Responsive table scroll
  const tableContainer = document.querySelector('.table-container');
  if (tableContainer) {
    // Add scroll indicators if needed
    function updateScrollIndicators() {
      const scrollLeft = tableContainer.scrollLeft;
      const scrollWidth = tableContainer.scrollWidth;
      const clientWidth = tableContainer.clientWidth;

      // You can add visual indicators for horizontal scroll here
      if (scrollLeft > 0) {
        tableContainer.classList.add('scrolled-left');
      } else {
        tableContainer.classList.remove('scrolled-left');
      }

      if (scrollLeft < scrollWidth - clientWidth) {
        tableContainer.classList.add('scrolled-right');
      } else {
        tableContainer.classList.remove('scrolled-right');
      }
    }

    tableContainer.addEventListener('scroll', updateScrollIndicators);
    window.addEventListener('resize', updateScrollIndicators);
    updateScrollIndicators(); // Initial check
  }

  // Format numbers in table cells
  const priceCells = document.querySelectorAll('.data-cell:nth-child(7), .data-cell:nth-child(8)');
  priceCells.forEach(cell => {
    const text = cell.textContent.trim();
    if (text && !isNaN(text.replace(/,/g, ''))) {
      const number = parseInt(text.replace(/,/g, ''));
      cell.textContent = number.toLocaleString('vi-VN');
    }
  });
});

// Utility functions
function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
}

function formatDate(date) {
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date);
}

// Export functions for potential use in other modules
window.PharmacyApp = {
  formatCurrency,
  formatDate
};
