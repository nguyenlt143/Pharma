// Print invoice functionality - matching POS template exactly
function printInvoice() {
    const printWindow = window.open('', '_blank', 'width=800,height=600');

    if (!printWindow) {

        showToast('Vui lòng cho phép popup để in hóa đơn', 'warning');
        return;
    }

    // Get invoice data from page
    const infoSections = document.querySelectorAll('.info-section');

    // Extract branch info
    const branchName = infoSections[0].querySelector('p:nth-of-type(1)').textContent.replace('Tên:', '').trim();
    const branchAddress = infoSections[0].querySelector('p:nth-of-type(2)').textContent.replace('Địa chỉ:', '').trim();

    // Extract customer info
    const customerName = infoSections[1].querySelector('p:nth-of-type(1)').textContent.replace('Tên:', '').trim();
    const customerPhone = infoSections[1].querySelector('p:nth-of-type(2)').textContent.replace('Số điện thoại:', '').trim();

    // Extract invoice info
    const invoiceCode = infoSections[2].querySelector('p:nth-of-type(1)').textContent.replace('Mã hóa đơn:', '').trim();
    const dateTimeStr = infoSections[2].querySelector('p:nth-of-type(2)').textContent.replace('Ngày tạo:', '').trim();
    const paymentMethod = infoSections[2].querySelector('p:nth-of-type(3)').textContent.replace('Thanh toán:', '').trim();
    const totalAmountStr = infoSections[2].querySelector('p:nth-of-type(4)').textContent.replace('Tổng tiền:', '').replace('VNĐ', '').trim();
    const totalAmount = parseFloat(totalAmountStr.replace(/,/g, ''));

    // Extract note if exists
    const noteElements = infoSections[2].querySelectorAll('p');
    let note = '';
    if (noteElements.length > 4) {
        note = noteElements[4].textContent.replace('Ghi chú:', '').trim();
    }

    // Parse date time
    const [datePart, timePart] = dateTimeStr.split(' ');
    const formattedDate = datePart;
    const formattedTime = timePart || '';

    // Build items HTML from table
    let itemsHTML = '';
    const tableRows = document.querySelectorAll('.medicine-list tbody tr');
    tableRows.forEach((row, index) => {
        const cells = row.querySelectorAll('td');
        const medicineName = cells[1].textContent.trim();
        const unitName = cells[2].textContent.trim();
        const quantity = cells[3].textContent.trim();
        const unitPrice = cells[4].textContent.replace('VNĐ', '').trim();
        const itemTotal = cells[5].textContent.replace('VNĐ', '').trim();

        itemsHTML += '<tr>' +
            '<td style="padding: 8px 4px; border-bottom: 1px dashed #ddd; text-align: center;">' + (index + 1) + '</td>' +
            '<td style="padding: 8px 4px; border-bottom: 1px dashed #ddd;">' + medicineName + '</td>' +
            '<td style="padding: 8px 4px; border-bottom: 1px dashed #ddd; text-align: center;">' + quantity + '</td>' +
            '<td style="padding: 8px 4px; border-bottom: 1px dashed #ddd; text-align: center;">' + unitName + '</td>' +
            '<td style="padding: 8px 4px; border-bottom: 1px dashed #ddd; text-align: right;">' + unitPrice + '</td>' +
            '<td style="padding: 8px 4px; border-bottom: 1px dashed #ddd; text-align: right;">' + itemTotal + '</td>' +
            '</tr>';
    });

    const invoiceHTML = '<!DOCTYPE html>' +
        '<html lang="vi">' +
        '<head>' +
            '<meta charset="UTF-8">' +
            '<title>Hóa đơn - ' + invoiceCode + '</title>' +
            '<style>' +
                '* { margin: 0; padding: 0; box-sizing: border-box; }' +
                'body { font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; padding: 20px; max-width: 800px; margin: 0 auto; background: #f5f5f5; }' +
                '.invoice-container { background: white; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.1); border-radius: 8px; }' +
                '.invoice-header { text-align: center; border-bottom: 3px solid #4338ca; padding-bottom: 20px; margin-bottom: 25px; }' +
                '.store-name { font-size: 28px; font-weight: bold; color: #4338ca; margin-bottom: 5px; }' +
                '.store-info { font-size: 13px; color: #666; line-height: 1.6; }' +
                '.invoice-title { font-size: 24px; font-weight: bold; color: #1f2937; margin: 20px 0 10px; text-align: center; }' +
                '.invoice-meta { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 25px; padding: 15px; background: #f9fafb; border-radius: 6px; }' +
                '.meta-item { font-size: 13px; }' +
                '.meta-label { font-weight: 600; color: #4b5563; display: inline-block; width: 120px; }' +
                '.meta-value { color: #1f2937; }' +
                '.invoice-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }' +
                '.invoice-table th { background: #4338ca; color: white; padding: 12px 8px; text-align: left; font-size: 13px; font-weight: 600; }' +
                '.invoice-table th:first-child, .invoice-table td:first-child { text-align: center; width: 40px; }' +
                '.invoice-table th:nth-child(3), .invoice-table td:nth-child(3), .invoice-table th:nth-child(4), .invoice-table td:nth-child(4) { text-align: center; width: 80px; }' +
                '.invoice-table th:nth-child(5), .invoice-table td:nth-child(5), .invoice-table th:nth-child(6), .invoice-table td:nth-child(6) { text-align: right; width: 120px; }' +
                '.invoice-table td { padding: 8px 4px; font-size: 13px; color: #374151; }' +
                '.invoice-summary { margin-top: 20px; border-top: 2px solid #e5e7eb; padding-top: 15px; }' +
                '.summary-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 14px; }' +
                '.summary-row.total { font-size: 18px; font-weight: bold; color: #4338ca; border-top: 2px solid #4338ca; padding-top: 12px; margin-top: 8px; }' +
                '.invoice-note { margin-top: 20px; padding: 12px; background: #fffbeb; border-left: 4px solid #f59e0b; border-radius: 4px; }' +
                '.note-title { font-weight: 600; color: #92400e; margin-bottom: 5px; font-size: 13px; }' +
                '.note-content { color: #78350f; font-size: 13px; line-height: 1.5; }' +
                '.invoice-footer { margin-top: 30px; text-align: center; font-size: 12px; color: #6b7280; border-top: 1px dashed #d1d5db; padding-top: 20px; }' +
                '.thank-you { font-size: 16px; font-weight: 600; color: #4338ca; margin-bottom: 10px; }' +
                '.print-button { background: #4338ca; color: white; border: none; padding: 12px 24px; border-radius: 6px; font-size: 14px; font-weight: 600; cursor: pointer; margin: 20px auto; display: block; }' +
                '.print-button:hover { background: #3730a3; }' +
                '@media print { body { background: white; padding: 0; } .invoice-container { box-shadow: none; padding: 10px; } .no-print { display: none !important; } }' +
                '@page { margin: 1cm; }' +
            '</style>' +
        '</head>' +
        '<body>' +
            '<div class="invoice-container">' +
                '<div class="invoice-header">' +
                    '<div class="store-name">' + branchName + '</div>' +
                    '<div class="store-info">Địa chỉ: ' + branchAddress + '</div>' +
                '</div>' +
                '<div class="invoice-title">HÓA ĐƠN BÁN HÀNG</div>' +
                '<div class="invoice-meta">' +
                    '<div class="meta-item"><span class="meta-label">Mã hóa đơn:</span><span class="meta-value"><strong>' + invoiceCode + '</strong></span></div>' +
                    '<div class="meta-item"><span class="meta-label">Ngày:</span><span class="meta-value">' + formattedDate + ' ' + formattedTime + '</span></div>' +
                    '<div class="meta-item"><span class="meta-label">Khách hàng:</span><span class="meta-value">' + customerName + '</span></div>' +
                    '<div class="meta-item"><span class="meta-label">Số điện thoại:</span><span class="meta-value">' + customerPhone + '</span></div>' +
                    '<div class="meta-item"><span class="meta-label">Thanh toán:</span><span class="meta-value">' + paymentMethod + '</span></div>' +
                    '<div class="meta-item"><span class="meta-label">Nhân viên:</span><span class="meta-value">' + (window.currentUserName || 'Nhân viên bán hàng') + '</span></div>' +
                '</div>' +
                '<table class="invoice-table">' +
                    '<thead><tr><th>STT</th><th>Tên thuốc</th><th>SL</th><th>ĐVT</th><th>Đơn giá</th><th>Thành tiền</th></tr></thead>' +
                    '<tbody>' + itemsHTML + '</tbody>' +
                '</table>' +
                '<div class="invoice-summary">' +
                    '<div class="summary-row"><span>Tổng cộng:</span><span>' + totalAmount.toLocaleString('vi-VN') + ' ₫</span></div>' +
                    '<div class="summary-row total"><span>TỔNG THANH TOÁN:</span><span>' + totalAmount.toLocaleString('vi-VN') + ' ₫</span></div>' +
                '</div>' +
                (note ? '<div class="invoice-note"><div class="note-title">📝 Ghi chú:</div><div class="note-content">' + note + '</div></div>' : '') +
                '<div class="invoice-footer">' +
                    '<div class="thank-you">Cảm ơn quý khách! Hẹn gặp lại!</div>' +
                    '<div>Hóa đơn được tạo từ hệ thống POS</div>' +
                    '<div style="margin-top: 5px;">Liên hệ: (028) 1234 5678</div>' +
                '</div>' +
            '</div>' +
            '<button class="print-button no-print" onclick="window.print()">🖨️ In hóa đơn</button>' +
            '<script>setTimeout(function() { window.print(); }, 500);</script>' +
        '</body>' +
        '</html>';

    printWindow.document.write(invoiceHTML);
    printWindow.document.close();
}

// Add keyboard shortcuts
document.addEventListener('keydown', function(event) {
    // Ctrl+P for print
    if (event.ctrlKey && event.key === 'p') {
        event.preventDefault();
        printInvoice();
    }
});

// Initialize
console.log('Invoice detail print functionality loaded');
