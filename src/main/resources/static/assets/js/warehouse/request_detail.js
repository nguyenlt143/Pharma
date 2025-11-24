// Hàm đóng modal và quay về trang trước
function closeModal() {
    console.log('Đóng modal, quay về trang trước');
    history.back(); // trở về URL trước
}

// Xử lý từ chối yêu cầu
function rejectRequest(id) {
    console.log('Từ chối yêu cầu với ID:', id);
    // Thêm logic thực sự xử lý từ chối ở đây
    alert('Yêu cầu đã được từ chối');
}

// Xử lý tạo phiếu xuất
function createExportSlip(id) {
    console.log('Tạo phiếu xuất cho ID:', id);
    // Thêm logic thực sự tạo phiếu xuất ở đây
    alert('Đã tạo phiếu xuất thành công');
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
