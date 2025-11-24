@import vn.edu.fpt.pharma.dto.warehouse.ExportCreateDTO

@param ExportCreateDTO exportData

@template.layouts.warehouse(
headContent = @`
    <title>Tạo Phiếu Xuất Kho</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/warehouse/export_create.css">
`,
content = @`
    <div class="container">
        <div class="frame">
            <main class="main-content">
                <h1 class="heading">Tạo Phiếu Xuất Kho</h1>
                <p class="subtitle">Điền thông tin phiếu và danh sách thuốc cần xuất.</p>

                <div class="form-section">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">Chi nhánh nhận</label>
                            <input type="text" class="form-input" id="branchName"
                                   value="${exportData != null ? exportData.getBranchName() : ""}" readonly>
                            <input type="hidden" id="branchId"
                                   value="${exportData != null ? String.valueOf(exportData.getBranchId()) : ""}">
                            <input type="hidden" id="requestId"
                                   value="${exportData != null && exportData.getRequestId() != null ? String.valueOf(exportData.getRequestId()) : ""}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Ngày tạo phiếu</label>
                            <div class="date-input">
                                <input type="date" class="form-input date-field" id="createdDate"
                                       value="${exportData != null && exportData.getCreatedDate() != null ? exportData.getCreatedDate().toString() : java.time.LocalDate.now().toString()}">
                            </div>
                        </div>
                    </div>

                    <div class="form-group full-width">
                        <label class="form-label">Ghi chú</label>
                        <textarea class="form-textarea" id="note" placeholder="Thêm ghi chú nếu cần">${exportData != null && exportData.getNote() != null ? exportData.getNote() : ""}</textarea>
                    </div>
                </div>

                <div class="table-section">
                    <h2 class="table-title">Danh sách thuốc & Lô hàng</h2>

                    <table class="medicine-table">
                        <thead>
                            <tr>
                                <th style="width: 60px;">STT</th>
                                <th>Tên thuốc</th>
                                <th style="width: 100px;">ĐVT</th>
                                <th style="width: 120px;">Hàm lượng</th>
                                <th style="width: 120px;">SL yêu cầu</th>
                                <th style="width: 120px;">Số lô</th>
                                <th style="width: 100px;">SL tồn kho</th>
                                <th style="width: 120px;">Giá bán</th>
                                <th style="width: 120px;">SL gửi</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if(exportData != null && exportData.getMedicines() != null)
                                @for(var medicine : exportData.getMedicines())
                                    @for(var i = 0; i < medicine.getBatches().size(); i++)
                                        !{var batch = medicine.getBatches().get(i);}
                                        <tr class="@if(i == 0)medicine-row@else batch-row@endif"
                                            data-variant-id="${medicine.getVariantId()}">
                                            @if(i == 0)
                                                <td rowspan="${medicine.getBatches().size()}" class="center-text">
                                                    ${exportData.getMedicines().indexOf(medicine) + 1}
                                                </td>
                                                <td rowspan="${medicine.getBatches().size()}" class="medicine-name">
                                                    ${medicine.getMedicineName()}
                                                </td>
                                                <td rowspan="${medicine.getBatches().size()}" class="center-text">
                                                    ${medicine.getUnit()}
                                                </td>
                                                <td rowspan="${medicine.getBatches().size()}" class="center-text">
                                                    ${medicine.getConcentration()}
                                                </td>
                                                <td rowspan="${medicine.getBatches().size()}" class="center-text requested-qty">
                                                    ${medicine.getRequestedQuantity()}
                                                </td>
                                            @endif
                                            <td class="batch-code">${batch.getBatchCode()}</td>
                                            <td class="center-text available-qty">${batch.getAvailableQuantity()}</td>
                                            <td class="right-text">
                                                ${String.format("%,.0f", batch.getBranchPrice())}
                                            </td>
                                            <td>
                                                <input type="number"
                                                       class="qty-input"
                                                       min="0"
                                                       max="${batch.getAvailableQuantity()}"
                                                       value="0"
                                                       data-inventory-id="${batch.getInventoryId()}"
                                                       data-batch-id="${batch.getBatchId()}"
                                                       data-price="${batch.getBranchPrice()}"
                                                       data-variant-id="${medicine.getVariantId()}">
                                            </td>
                                        </tr>
                                    @endfor
                                @endfor
                            @else
                                <tr>
                                    <td colspan="9" class="center-text">Không có dữ liệu</td>
                                </tr>
                            @endif
                        </tbody>
                    </table>

                    <div class="total-section">
                        <span class="total-label">TỔNG SỐ TIỀN:</span>
                        <span class="total-amount" id="totalAmount">0</span>
                    </div>
                </div>

                <div class="button-section">
                    <button class="btn-secondary" onclick="saveDraft()">Lưu nháp</button>
                    <button class="btn-primary" onclick="createExport()">Tạo phiếu xuất</button>
                </div>
            </main>
        </div>
    </div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/export_create.js" defer></script>
`
)

