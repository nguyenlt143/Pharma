// Warehouse Management UI JavaScript
document.addEventListener('DOMContentLoaded', function() {
    console.log('Warehouse Management UI loaded successfully');

    // Initialize the application
    initializeApp();
});

function initializeApp() {
    // Handle responsive behavior
    handleResponsiveLayout();

    // Add window resize listener
    window.addEventListener('resize', handleResponsiveLayout);
}

function handleResponsiveLayout() {
    const container = document.querySelector('.container');
    const mainFrame = document.querySelector('.main-frame');

    if (window.innerWidth <= 1920) {
        container.style.width = '100%';
        container.style.height = '100vh';
        mainFrame.style.position = 'relative';
        mainFrame.style.top = '0';
        mainFrame.style.left = '0';
    } else {
        container.style.width = '2120px';
        container.style.height = '1400px';
        mainFrame.style.position = 'absolute';
        mainFrame.style.top = '100px';
        mainFrame.style.left = '100px';
    }
}

// Image loading handler
function handleImageLoad() {
    const backgroundImage = document.querySelector('.background-image');
    const img = new Image();

    img.onload = function() {
        console.log('Background image loaded successfully');
        backgroundImage.style.opacity = '1';
    };

    img.onerror = function() {
        console.warn('Background image failed to load');
        backgroundImage.style.backgroundColor = '#f5f5f5';
    };

    img.src = 'https://static.codia.ai/image/2025-11-03/SBqHW31wFY.png';
}

// Initialize image loading
handleImageLoad();
