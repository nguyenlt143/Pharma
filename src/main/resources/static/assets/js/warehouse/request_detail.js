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
            alert('Yêu cầu đã được từ chối');
            // Reload the page to show updated status
            window.location.reload();
        } else {
            alert('Có lỗi xảy ra khi từ chối yêu cầu');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi từ chối yêu cầu');
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
            alert('Yêu cầu đã được đồng ý');
            // Reload the page to show updated status
            window.location.reload();
        } else {
            alert('Có lỗi xảy ra khi đồng ý yêu cầu');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi đồng ý yêu cầu');
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
