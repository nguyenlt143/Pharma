// Toggle dropdown menu
function toggleDropdown(button) {
  const dropdown = button.nextElementSibling;
  const isVisible = dropdown.classList.contains('show');

  // Close all other dropdowns
  document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
    menu.classList.remove('show');
  });

  // Toggle current dropdown
  if (!isVisible) {
    dropdown.classList.add('show');
  }
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
  if (!event.target.closest('.dropdown-container')) {
    document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
      menu.classList.remove('show');
    });
  }
});


// Handle search functionality
document.querySelector('.search-button').addEventListener('click', function() {
  const searchInput = document.querySelector('.search-container input');
  const searchTerm = searchInput.value.trim();

  if (searchTerm) {
    console.log('Searching for:', searchTerm);
    // Add your search logic here
  }
});

// Handle filter changes
document.querySelectorAll('select').forEach(select => {
  select.addEventListener('change', function() {
    console.log('Filter changed:', this.name, this.value);
    // Add your filter logic here
  });
});

// Handle date inputs
document.querySelectorAll('.input-container input[type="text"]').forEach(input => {
  input.addEventListener('click', function() {
    // Add date picker functionality here
    console.log('Date input clicked');
  });
});

// Initialize tooltips or other interactive elements
document.addEventListener('DOMContentLoaded', function() {
  console.log('Pharmacy Management System loaded');

  // Add any initialization code here

  // Example: Set active navigation item
  const firstNavLink = document.querySelector('.nav-link');
  if (firstNavLink) {
    firstNavLink.classList.add('active');
  }
});

// Handle table row selection
document.querySelectorAll('.invoice-table tbody tr').forEach(row => {
  row.addEventListener('click', function(e) {
    // Don't select row if clicking on dropdown button
    if (e.target.closest('.action-button') || e.target.closest('.dropdown-menu')) {
      return;
    }

    // Remove selection from other rows
    document.querySelectorAll('.invoice-table tbody tr.selected').forEach(r => {
      r.classList.remove('selected');
    });

    // Add selection to current row
    this.classList.add('selected');
  });
});

// Add keyboard shortcuts
document.addEventListener('keydown', function(e) {
  // F3 for search
  if (e.key === 'F3') {
    e.preventDefault();
    const searchInput = document.querySelector('.search-container input');
    searchInput.focus();
  }

  // Escape to close dropdowns
  if (e.key === 'Escape') {
    document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
      menu.classList.remove('show');
    });
  }
});