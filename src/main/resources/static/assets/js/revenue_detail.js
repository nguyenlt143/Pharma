document.addEventListener('DOMContentLoaded', function() {
  // Mobile menu toggle
  const menuButton = document.querySelector('.menu-button');
  const sidebar = document.querySelector('.sidebar');

  if (menuButton && sidebar) {
    menuButton.addEventListener('click', function() {
      sidebar.classList.toggle('open');
    });
  }

  // Close sidebar when clicking outside on mobile
  document.addEventListener('click', function(event) {
    if (window.innerWidth <= 768) {
      if (!sidebar.contains(event.target) && !menuButton.contains(event.target)) {
        sidebar.classList.remove('open');
      }
    }
  });

  // Navigation link interactions
  const navLinks = document.querySelectorAll('.nav-link');
  navLinks.forEach(link => {
    link.addEventListener('click', function(e) {
      e.preventDefault();

      // Remove active class from all links
      navLinks.forEach(l => l.classList.remove('active'));

      // Add active class to clicked link
      this.classList.add('active');

      // Close mobile menu if open
      if (window.innerWidth <= 768) {
        sidebar.classList.remove('open');
      }
    });
  });

  // Control buttons functionality
  const pauseButton = document.querySelector('.pause-button');
  const stopButton = document.querySelector('.stop-button');

  if (pauseButton) {
    pauseButton.addEventListener('click', function() {
      console.log('Pause button clicked');
      // Add pause functionality here
    });
  }

  if (stopButton) {
    stopButton.addEventListener('click', function() {
      console.log('Stop button clicked');
      // Add stop functionality here
    });
  }

  // Date input functionality
  const dateInput = document.querySelector('.date-input');
  if (dateInput) {
    dateInput.addEventListener('click', function() {
      // You can integrate a date picker library here
      console.log('Date input clicked');
    });
  }

  // Dropdown button functionality
  const dropdownButtons = document.querySelectorAll('.dropdown-button, .user-dropdown');
  dropdownButtons.forEach(button => {
    button.addEventListener('click', function() {
      console.log('Dropdown button clicked');
      // Add dropdown menu functionality here
    });
  });

  // Back button functionality
  const backButton = document.querySelector('.back-button');
  if (backButton) {
    backButton.addEventListener('click', function() {
      console.log('Back button clicked');
      // Add navigation back functionality here
    });
  }

  // Play button functionality
  const playButton = document.querySelector('.play-button');
  if (playButton) {
    playButton.addEventListener('click', function() {
      console.log('Play button clicked');
      // Add play functionality here
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
