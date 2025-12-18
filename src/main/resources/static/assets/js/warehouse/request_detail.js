// Toast notification helper
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'error' ? '#dc3545' : type === 'success' ? '#28a745' : '#17a2b8'};
        color: white;
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// Hàm đóng modal và quay về trang trước
function closeModal() {
    console.log('Đóng modal, quay về trang trước');
    history.back(); // trở về URL trước
}

// Xử lý từ chối yêu cầu
function rejectRequest(id) {
    if (!confirm('Bạn có chắc chắn muốn từ chối yêu cầu này?')) {
        return;
    }

    fetch(`/warehouse/request/cancel/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            showToast('Yêu cầu đã được từ chối', 'success');
            // Reload the page to show updated status
            setTimeout(() => window.location.reload(), 1500);
        } else {
            showToast('Có lỗi xảy ra khi từ chối yêu cầu', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Có lỗi xảy ra khi từ chối yêu cầu', 'error');
    });
}

// Xử lý đồng ý yêu cầu (chuyển trạng thái sang CONFIRMED)
function approveRequest(id) {
    if (!confirm('Bạn có chắc chắn muốn đồng ý yêu cầu này?')) {
        return;
    }

    fetch(`/warehouse/request/confirm/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            showToast('Yêu cầu đã được đồng ý', 'success');
            // Reload the page to show updated status
            setTimeout(() => window.location.reload(), 1500);
        } else {
            showToast('Có lỗi xảy ra khi đồng ý yêu cầu', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Có lỗi xảy ra khi đồng ý yêu cầu', 'error');
    });
}

// Xử lý tạo phiếu xuất (không thay đổi trạng thái)
function createExportSlip(id) {
    console.log('Tạo phiếu xuất cho ID:', id);
    // Chuyển sang trang tạo phiếu xuất với request ID
    window.location.href = `/warehouse/export/create?requestId=${id}`;
}

// ESC key để đóng modal
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeModal();
    }
});

// Click ra ngoài modal để đóng
const overlay = document.querySelector('.overlay');
if (overlay) {
    overlay.addEventListener('click', function(event) {
        if (event.target === this) {
            closeModal();
        }
    });
}

// Ngăn click vào nội dung modal đóng modal
const modal = document.querySelector('.modal');
if (modal) {
    modal.addEventListener('click', function(event) {
        event.stopPropagation();
    });
}
