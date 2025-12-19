/**
 * Error Handler Utility
 * Xử lý lỗi từ API responses một cách thống nhất
 *
 * Usage:
 * <script src="/assets/js/utils/errorHandler.js"></script>
 *
 * Trong các file JS khác:
 * const response = await fetch(...);
 * if (!response.ok) {
 *     throw await parseErrorResponse(response);
 * }
 */

/**
 * Parse error response từ fetch API
 * @param {Response} response - Fetch Response object
 * @returns {Promise<Error>} Error object với message chi tiết
 */
async function parseErrorResponse(response) {
    try {
        const contentType = response.headers.get('content-type');

        // Xử lý JSON response
        if (contentType && contentType.includes('application/json')) {
            const errorData = await response.json();

            // Xử lý validation errors (có field errors)
            if (errorData.errors && typeof errorData.errors === 'object') {
                const errorMessages = Object.entries(errorData.errors)
                    .map(([field, message]) => `${field}: ${message}`)
                    .join('\n');
                return new Error(errorMessages);
            }

            // Xử lý message thông thường
            return new Error(errorData.message || 'Có lỗi xảy ra');
        }

        // Fallback cho text response
        const errorText = await response.text();
        return new Error(errorText || 'Có lỗi xảy ra');

    } catch (parseError) {
        // Nếu không parse được response
        return new Error('Có lỗi xảy ra khi xử lý phản hồi từ server');
    }
}

/**
 * Wrapper cho fetch API với xử lý lỗi tự động
 * @param {string} url - URL endpoint
 * @param {Object} options - Fetch options
 * @returns {Promise<any>} Response data
 * @throws {Error} Error với message chi tiết
 */
async function fetchWithErrorHandling(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        throw await parseErrorResponse(response);
    }

    // Nếu response là 204 No Content, không cần parse JSON
    if (response.status === 204) {
        return null;
    }

    // Parse JSON response
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return await response.json();
    }

    return await response.text();
}

/**
 * Hiển thị toast notification (unified version)
 * @param {string} message - Message để hiển thị
 * @param {number} timeout - Thời gian hiển thị (ms)
 * @param {string} type - Loại toast: 'info', 'success', 'error'
 */
function showToastNotification(message, timeout = 2500, type = 'info') {
    const toastEl = document.getElementById('toast');

    if (!toastEl) {
        console.error('Toast element not found - using global toast system');
        // Use global toast system as fallback
        if (window.showToast) {
            window.showToast(message, type, timeout);
        } else {
            console.error('Toast not available:', message);
        }
        return;
    }

    toastEl.textContent = message;
    toastEl.classList.remove('hidden', 'success', 'error', 'show');
    toastEl.style.display = 'block';

    // Force reflow để đảm bảo animation hoạt động
    void toastEl.offsetWidth;

    toastEl.classList.add('show');

    if (type === 'success') {
        toastEl.classList.add('success');
    } else if (type === 'error') {
        toastEl.classList.add('error');
    }

    setTimeout(() => {
        toastEl.classList.remove('show');
        setTimeout(() => {
            toastEl.classList.add('hidden');
            toastEl.style.display = 'none';
        }, 250);
    }, timeout);
}

