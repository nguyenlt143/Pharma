document.addEventListener('DOMContentLoaded', function() {
  // Tab functionality
  const tabButtons = document.querySelectorAll('.tab-button');
  const editForm = document.getElementById('editForm');
  const successMessage = document.getElementById('successMessage');

  // Tab switching
  tabButtons.forEach(button => {
    button.addEventListener('click', function() {
      // Remove active class from all tabs
      tabButtons.forEach(tab => tab.classList.remove('active'));
      // Add active class to clicked tab
      this.classList.add('active');

      // Here you could implement different form content based on the active tab
      const tabText = this.textContent.trim();
      console.log('Active tab:', tabText);
    });
  });

  // Form submission
  editForm.addEventListener('submit', function(e) {
    // Get form data
    const formData = new FormData(editForm);
    const data = {
      fullName: document.getElementById('fullName').value,
      birthDate: document.getElementById('birthDate').value,
      gender: document.getElementById('gender').value,
      phone: document.getElementById('phone').value,
      address: document.getElementById('address').value
    };

    console.log('Form data:', data);

  // Cancel button functionality
  const cancelButton = document.querySelector('.btn-secondary');
  cancelButton.addEventListener('click', function() {
    // Reset form to original values
    document.getElementById('fullName').value = 'Nguyễn Văn An';
    document.getElementById('birthDate').value = '1990-01-15';
    document.getElementById('gender').value = 'male';
    document.getElementById('phone').value = '0901234567';
    document.getElementById('address').value = '123 Đường ABC, Phường X, Quận Y, TP. HCM';

    // Hide success message if visible
    successMessage.classList.remove('show');
  });

  // Navigation item click handlers
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(item => {
    item.addEventListener('click', function() {
      const navText = this.querySelector('.nav-text')?.textContent;
      if (navText) {
        console.log('Navigation clicked:', navText);
        // Here you could implement navigation logic
      }
    });
  });

  // Form validation
  const inputs = document.querySelectorAll('.form-input, .form-select');
  inputs.forEach(input => {
    input.addEventListener('blur', function() {
      validateField(this);
    });
  });

  function validateField(field) {
    const value = field.value.trim();

    // Remove existing error styling
    field.classList.remove('error');

    // Basic validation
    if (field.hasAttribute('required') && !value) {
      field.classList.add('error');
      return false;
    }

    // Email validation
    if (field.type === 'email' && value) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(value)) {
        field.classList.add('error');
        return false;
      }
    }

    // Phone validation
    if (field.type === 'tel' && value) {
      const phoneRegex = /^[0-9]{10,11}$/;
      if (!phoneRegex.test(value.replace(/\s/g, ''))) {
        field.classList.add('error');
        return false;
      }
    }

    return true;
  }

  // Add error styles to CSS dynamically
  const style = document.createElement('style');
  style.textContent = `
    .form-input.error,
    .form-select.error {
      border-color: #dc3545;
      background-color: #fff5f5;
    }

    .form-input.error:focus,
    .form-select.error:focus {
      border-color: #dc3545;
      box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
    }
  `;
  document.head.appendChild(style);

  // Responsive sidebar toggle for mobile
  function createMobileToggle() {
    if (window.innerWidth <= 768) {
      const sidebar = document.querySelector('.sidebar');
      const mainContent = document.querySelector('.main-content');

      // Create toggle button if it doesn't exist
      let toggleButton = document.querySelector('.sidebar-toggle');
      if (!toggleButton) {
        toggleButton = document.createElement('button');
        toggleButton.className = 'sidebar-toggle';
        toggleButton.innerHTML = '☰';
        toggleButton.style.cssText = `
          position: fixed;
          top: 20px;
          left: 20px;
          z-index: 1000;
          background: #2e86de;
          color: white;
          border: none;
          border-radius: 8px;
          width: 40px;
          height: 40px;
          font-size: 18px;
          cursor: pointer;
          display: none;
        `;
        document.body.appendChild(toggleButton);

        toggleButton.addEventListener('click', function() {
          sidebar.classList.toggle('mobile-open');
        });
      }

      // Show toggle button on mobile
      toggleButton.style.display = 'block';

      // Add mobile styles
      if (!document.querySelector('#mobile-styles')) {
        const mobileStyle = document.createElement('style');
        mobileStyle.id = 'mobile-styles';
        mobileStyle.textContent = `
          @media (max-width: 768px) {
            .sidebar {
              position: fixed;
              top: 0;
              left: -100%;
              height: 100vh;
              width: 280px;
              z-index: 999;
              transition: left 0.3s ease;
              flex-direction: column;
              justify-content: space-between;
            }

            .sidebar.mobile-open {
              left: 0;
            }

            .main-content {
              margin-left: 0;
              width: 100%;
            }
          }
        `;
        document.head.appendChild(mobileStyle);
      }
    }
  }

  // Initialize mobile toggle
  createMobileToggle();

  // Re-initialize on window resize
  window.addEventListener('resize', createMobileToggle);
});
