document.addEventListener('DOMContentLoaded', function() {
  // Menu button functionality
  const menuButton = document.querySelector('.menu-button');
  const sidebar = document.querySelector('.sidebar');

  menuButton.addEventListener('click', function() {
    sidebar.classList.toggle('collapsed');
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

      // Update main content based on selection
      const linkText = this.querySelector('.nav-text').textContent;
      console.log('Navigated to:', linkText);
    });
  });

  // Dropdown functionality
  const dropdownButtons = document.querySelectorAll('.dropdown-button, .user-dropdown');
  dropdownButtons.forEach(button => {
    button.addEventListener('click', function() {
      console.log('Dropdown clicked');
      // Add dropdown menu functionality here
    });
  });

  // Control select changes
  const controlSelects = document.querySelectorAll('.control-select');
  controlSelects.forEach(select => {
    select.addEventListener('change', function() {
      console.log('Filter changed:', this.value);
      // Update table data based on selection
      updateScheduleTable();
    });
  });

  // Return button functionality
  const returnButton = document.querySelector('.return-button');
  returnButton.addEventListener('click', function() {
    console.log('Return button clicked');
    // Navigate back or close current view
  });

  // Back button in sidebar
  const backButton = document.querySelector('.back-button');
  backButton.addEventListener('click', function() {
    console.log('Back button clicked');
    // Navigate back functionality
  });

  // Play button functionality
  const playButton = document.querySelector('.play-button');
  playButton.addEventListener('click', function() {
    console.log('Play button clicked');
    // Start/stop functionality
  });

  // Function to update schedule table based on filters
  function updateScheduleTable() {
    const yearSelect = document.querySelector('.control-select');
    const weekSelect = document.querySelector('.week-select');

    const selectedYear = yearSelect.value;
    const selectedWeek = weekSelect.value;

    console.log('Updating table for:', selectedYear, selectedWeek);

    // Here you would typically fetch new data and update the table
    // For now, we'll just log the selection
  }

  // Add hover effects for interactive elements
  const interactiveElements = document.querySelectorAll('button, .nav-link, select');
  interactiveElements.forEach(element => {
    element.addEventListener('mouseenter', function() {
      this.style.transform = 'translateY(-1px)';
    });

    element.addEventListener('mouseleave', function() {
      this.style.transform = 'translateY(0)';
    });
  });

  // Initialize tooltips for icons
  const iconElements = document.querySelectorAll('.material-icons');
  iconElements.forEach(icon => {
    const parent = icon.parentElement;
    if (parent.classList.contains('nav-link')) {
      const text = parent.querySelector('.nav-text').textContent;
      icon.title = text;
    }
  });

  // Responsive sidebar toggle for mobile
  function handleResize() {
    if (window.innerWidth <= 768) {
      sidebar.classList.add('mobile');
    } else {
      sidebar.classList.remove('mobile');
    }
  }

  window.addEventListener('resize', handleResize);
  handleResize(); // Initial check

  // Add loading states for async operations
  function showLoading(element) {
    element.classList.add('loading');
    element.disabled = true;
  }

  function hideLoading(element) {
    element.classList.remove('loading');
    element.disabled = false;
  }

  // Simulate data loading
  setTimeout(() => {
    console.log('Application initialized successfully');
  }, 1000);
});

// Utility functions
function formatDate(date) {
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date);
}

function updateDateTime() {
  const now = new Date();
  const dateElement = document.querySelector('.date-text');
  if (dateElement) {
    dateElement.textContent = formatDate(now);
  }
}

// Update date/time every minute
setInterval(updateDateTime, 60000);
