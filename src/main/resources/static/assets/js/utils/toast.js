/**
 * Toast Notification System
 *
 * Unified toast notification component for the entire application.
 * Replaces all alert() calls with modern, non-blocking notifications.
 *
 * Usage:
 * <link rel="stylesheet" href="/assets/css/toast.css">
 * <script src="/assets/js/utils/toast.js"></script>
 *
 * showToast('Message', 'success|error|warning|info');
 */

(function(window) {
    'use strict';

    // Toast configuration
    const TOAST_CONFIG = {
        duration: 3000, // Default duration in milliseconds
        position: 'top-right', // Position on screen
        maxToasts: 5, // Maximum number of visible toasts
        closeButton: true // Show close button
    };

    // Toast container
    let toastContainer = null;

    /**
     * Initialize toast container
     */
    function initToastContainer() {
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.className = 'toast-container';
            toastContainer.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                display: flex;
                flex-direction: column;
                gap: 12px;
                pointer-events: none;
            `;
            document.body.appendChild(toastContainer);
        }
        return toastContainer;
    }

    /**
     * Create and show a toast notification
     * @param {string} message - Message to display
     * @param {string} type - Type: 'success', 'error', 'warning', 'info'
     * @param {number} duration - Duration in milliseconds (default: 3000)
     * @returns {HTMLElement} The toast element
     */
    function showToast(message, type = 'info', duration = TOAST_CONFIG.duration) {
        // Ensure container exists
        const container = initToastContainer();

        // Create toast element
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        // Create message element
        const messageEl = document.createElement('span');
        messageEl.textContent = message;
        messageEl.style.flex = '1';
        toast.appendChild(messageEl);

        // Add close button if enabled
        if (TOAST_CONFIG.closeButton) {
            const closeBtn = document.createElement('button');
            closeBtn.className = 'toast-close';
            closeBtn.innerHTML = '×';
            closeBtn.setAttribute('aria-label', 'Close');
            closeBtn.onclick = () => removeToast(toast);
            toast.appendChild(closeBtn);
        }

        // Remove oldest toast if max exceeded
        const toasts = container.querySelectorAll('.toast');
        if (toasts.length >= TOAST_CONFIG.maxToasts) {
            removeToast(toasts[0]);
        }

        // Add to container
        container.appendChild(toast);

        // Trigger animation
        requestAnimationFrame(() => {
            toast.classList.add('show');
        });

        // Auto-remove after duration
        if (duration > 0) {
            setTimeout(() => {
                removeToast(toast);
            }, duration);
        }

        return toast;
    }

    /**
     * Remove a toast with animation
     * @param {HTMLElement} toast - Toast element to remove
     */
    function removeToast(toast) {
        if (!toast || !toast.parentElement) return;

        toast.classList.remove('show');
        toast.classList.add('animate-out');

        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
        }, 300);
    }

    /**
     * Show success toast
     * @param {string} message - Success message
     * @param {number} duration - Duration in milliseconds
     */
    function showSuccess(message, duration) {
        return showToast(message, 'success', duration);
    }

    /**
     * Show error toast
     * @param {string} message - Error message
     * @param {number} duration - Duration in milliseconds
     */
    function showError(message, duration) {
        return showToast(message, 'error', duration);
    }

    /**
     * Show warning toast
     * @param {string} message - Warning message
     * @param {number} duration - Duration in milliseconds
     */
    function showWarning(message, duration) {
        return showToast(message, 'warning', duration);
    }

    /**
     * Show info toast
     * @param {string} message - Info message
     * @param {number} duration - Duration in milliseconds
     */
    function showInfo(message, duration) {
        return showToast(message, 'info', duration);
    }

    /**
     * Clear all toasts
     */
    function clearAllToasts() {
        if (toastContainer) {
            const toasts = toastContainer.querySelectorAll('.toast');
            toasts.forEach(toast => removeToast(toast));
        }
    }

    /**
     * Update toast configuration
     * @param {Object} config - Configuration object
     */
    function configureToast(config) {
        Object.assign(TOAST_CONFIG, config);
    }

    // Export to window
    window.showToast = showToast;
    window.showSuccess = showSuccess;
    window.showError = showError;
    window.showWarning = showWarning;
    window.showInfo = showInfo;
    window.clearAllToasts = clearAllToasts;
    window.configureToast = configureToast;

    // Backward compatibility with old showFieldError functions
    window.showFieldError = window.showFieldError || function(field, message) {
        let errorDiv = field.parentElement.querySelector('.invalid-feedback');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            errorDiv.style.display = 'block';
            errorDiv.style.color = '#dc3545';
            errorDiv.style.fontSize = '0.875rem';
            errorDiv.style.marginTop = '0.25rem';
            field.parentElement.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    };

    window.clearFieldError = window.clearFieldError || function(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentElement.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    };

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initToastContainer);
    } else {
        initToastContainer();
    }

})(window);

