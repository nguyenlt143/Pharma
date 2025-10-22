document.addEventListener('DOMContentLoaded', function() {
  // Menu toggle functionality
  const menuButton = document.querySelector('.menu-button');
  const sidebar = document.querySelector('.sidebar');

  if (menuButton && sidebar) {
    menuButton.addEventListener('click', function() {
      sidebar.classList.toggle('collapsed');
    });
  }

  // Date input functionality
  const dateInputs = document.querySelectorAll('.date-input');
  dateInputs.forEach(input => {
    input.addEventListener('click', function() {
      // In a real application, you would integrate with a date picker library
      console.log('Date picker would open here');
    });
  });

  // Search functionality
  const searchButton = document.querySelector('.search-button');
  const searchInput = document.querySelector('.search-input');

  if (searchButton && searchInput) {
    searchButton.addEventListener('click', function() {
      const searchTerm = searchInput.value.trim();
      if (searchTerm) {
        console.log('Searching for:', searchTerm);
        // In a real application, you would perform the search here
        performSearch(searchTerm);
      }
    });

    // Allow search on Enter key
    searchInput.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') {
        searchButton.click();
      }
    });
  }

  // F3 key for search focus
  document.addEventListener('keydown', function(e) {
    if (e.key === 'F3') {
      e.preventDefault();
      if (searchInput) {
        searchInput.focus();
      }
    }
  });

  // Pagination functionality
  const pageButtons = document.querySelectorAll('.page-button:not(.disabled)');
  const pageInput = document.querySelector('.page-input');

  pageButtons.forEach(button => {
    button.addEventListener('click', function() {
      const icon = this.querySelector('.material-icons').textContent;
      let currentPage = parseInt(pageInput.value) || 1;

      switch(icon) {
        case 'first_page':
          currentPage = 1;
          break;
        case 'chevron_left':
          currentPage = Math.max(1, currentPage - 1);
          break;
        case 'chevron_right':
          currentPage = currentPage + 1;
          break;
        case 'last_page':
          currentPage = 10; // Assuming 10 is the last page
          break;
      }

      pageInput.value = currentPage;
      loadPage(currentPage);
    });
  });

  // Records per page change
  const recordsSelect = document.querySelector('.records-select');
  if (recordsSelect) {
    recordsSelect.addEventListener('change', function() {
      const recordsPerPage = this.value;
      console.log('Records per page changed to:', recordsPerPage);
      // In a real application, you would reload the table with new page size
      loadPage(1, recordsPerPage);
    });
  }

  // Filter changes
  const filterSelects = document.querySelectorAll('.filter-select');
  filterSelects.forEach(select => {
    select.addEventListener('change', function() {
      console.log('Filter changed:', this.previousElementSibling.textContent, this.value);
      // In a real application, you would apply the filter
      applyFilters();
    });
  });

  // Back button functionality
  const backButtons = document.querySelectorAll('.back-button, .back-action-button');
  backButtons.forEach(button => {
    button.addEventListener('click', function() {
      console.log('Going back...');
      // In a real application, you would navigate back
      window.history.back();
    });
  });

  // Dropdown functionality
  const dropdownButtons = document.querySelectorAll('.dropdown-button, .user-dropdown');
  dropdownButtons.forEach(button => {
    button.addEventListener('click', function() {
      console.log('Dropdown clicked');
      // In a real application, you would show/hide dropdown menu
    });
  });

  // Navigation links
  const navLinks = document.querySelectorAll('.nav-link');
  navLinks.forEach(link => {
    link.addEventListener('click', function(e) {
      e.preventDefault();

      // Remove active class from all links
      navLinks.forEach(l => l.classList.remove('active'));

      // Add active class to clicked link
      this.classList.add('active');

      const linkText = this.querySelector('span:last-child').textContent;
      console.log('Navigating to:', linkText);

      // In a real application, you would navigate to the appropriate page
    });
  });
});

// Helper functions that would be implemented in a real application
function performSearch(searchTerm) {
  console.log('Performing search for:', searchTerm);
  // Implementation would filter the table data based on search term
}

function loadPage(pageNumber, recordsPerPage = 10) {
  console.log('Loading page:', pageNumber, 'with', recordsPerPage, 'records per page');
  // Implementation would load data for the specified page
}

function applyFilters() {
  console.log('Applying filters...');
  // Implementation would apply all current filter values to the data
}

// Utility function to format currency
function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'decimal',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount);
}

// Utility function to format date
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
}

// Add some interactive behaviors
document.addEventListener('DOMContentLoaded', function() {
  // Add hover effects to table rows
  const tableRows = document.querySelectorAll('.data-table tbody tr:not(.total-row)');
  tableRows.forEach(row => {
    row.addEventListener('mouseenter', function() {
      this.style.backgroundColor = '#f8fafc';
    });

    row.addEventListener('mouseleave', function() {
      if (this.classList.contains('alternate')) {
        this.style.backgroundColor = '#f9fafb';
      } else {
        this.style.backgroundColor = 'white';
      }
    });
  });

  // Add loading state simulation
  function showLoading() {
    const tableContainer = document.querySelector('.table-container');
    if (tableContainer) {
      tableContainer.style.opacity = '0.5';
      tableContainer.style.pointerEvents = 'none';
    }
  }

  function hideLoading() {
    const tableContainer = document.querySelector('.table-container');
    if (tableContainer) {
      tableContainer.style.opacity = '1';
      tableContainer.style.pointerEvents = 'auto';
    }
  }

  // Simulate loading when search is performed
  const originalPerformSearch = window.performSearch;
  window.performSearch = function(searchTerm) {
    showLoading();
    setTimeout(() => {
      hideLoading();
      if (originalPerformSearch) {
        originalPerformSearch(searchTerm);
      }
    }, 1000);
  };
});
