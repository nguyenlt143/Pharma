function closeModal() {
    console.log('Đóng modal');
    // In a real application, this would close the modal
    // For demo purposes, we'll just hide it
    document.querySelector('.overlay').style.display = 'none';
}

function rejectRequest() {
    console.log('Từ chối yêu cầu');
    // Handle reject request logic here
    alert('Yêu cầu đã được từ chối');
}

function createExportSlip() {
    console.log('Tạo phiếu xuất');
    // Handle create export slip logic here
    alert('Đã tạo phiếu xuất thành công');
}

// Add keyboard event listener for ESC key to close modal
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeModal();
    }
});

// Add click outside modal to close
document.querySelector('.overlay').addEventListener('click', function(event) {
    if (event.target === this) {
        closeModal();
    }
});

// Prevent modal content clicks from closing the modal
document.querySelector('.modal').addEventListener('click', function(event) {
    event.stopPropagation();
});
