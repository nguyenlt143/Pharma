
// Toggle dropdown user info
const userInfo = document.querySelector('.user-info');
if (userInfo) {
  const dropdownButton = userInfo.querySelector('.user-dropdown');

  dropdownButton.addEventListener('click', (event) => {
    event.stopPropagation();
    userInfo.classList.toggle('active');
  });

  document.addEventListener('click', (event) => {
    if (!userInfo.contains(event.target)) {
      userInfo.classList.remove('active');
    }
  });
}

// Navigation active highlight
const navLinks = document.querySelectorAll('.nav-link');
if (navLinks.length > 0) {
  navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      navLinks.forEach(l => l.classList.remove('active'));
      link.classList.add('active');
    });
  });

  // Đánh dấu link hiện tại khi load
  document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    navLinks.forEach(link => {
      if (link.getAttribute('href') === currentPath) {
        link.classList.add('active');
      }
    });
  });
}

// Menu toggle (ẩn/hiện sidebar)
const menuButton = document.querySelector('.menu-button');
const sidebar = document.querySelector('.sidebar');
if (menuButton && sidebar) {
  menuButton.addEventListener('click', () => {
    sidebar.classList.toggle('collapsed');
  });
}

// Back button
const backButton = document.querySelector('.back-button');
if (backButton) {
  backButton.addEventListener('click', () => {
    window.history.back();
  });
}

// Dropdown branch button (nếu có)
const dropdownButtons = document.querySelectorAll('.dropdown-button');
dropdownButtons.forEach(button => {
  button.addEventListener('click', () => {
    console.log('Dropdown branch clicked');
  });
});

// Set current date
document.addEventListener('DOMContentLoaded', () => {
  const dateElement = document.querySelector('.date-text');
  if (dateElement) {
    const currentDate = new Date();
    dateElement.textContent = currentDate.toLocaleDateString('vi-VN');
  }
});
