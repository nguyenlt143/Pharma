
  // Control select changes
  const controlSelects = document.querySelectorAll('.control-select');
  controlSelects.forEach(select => {
    select.addEventListener('change', function() {
      console.log('Filter changed:', this.value);
      // Update table data based on selection
      updateScheduleTable();
    });
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
