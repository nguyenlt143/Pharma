

@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines
@param Long branchId
@param String branchName

@template.layouts.warehouse(
headContent = @`
    <title>Danh Sách Thuốc Tồn Kho - ${branchName}</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
`,
content = @`
    <div class="p-6">
        <div class="flex justify-between items-center mb-6">
            <div>
                <h2 class="text-2xl font-bold text-gray-800">DANH SÁCH THUỐC TỒN KHO</h2>
                <p class="text-sm text-gray-600">Quản lý và theo dõi thuốc tồn kho tại kho tổng</p>
            </div>
            <div class="text-sm text-gray-700 flex items-center gap-2">
                <span class="material-icons-outlined text-base">warehouse</span>
                ${branchName} @if(medicines != null) | ${medicines.size()} loại@endif
            </div>
        </div>

        <!-- Filters -->
        <div class="bg-white p-4 rounded-lg shadow mb-6">
            <div class="flex flex-wrap items-center gap-4">
                <div class="relative">
                    <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                    <input id="searchInput" type="text" placeholder="Tìm theo tên / hoạt chất / mã lô" class="pl-10 pr-4 py-2 w-72 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                </div>
                <select id="categoryFilter" class="py-2 px-3 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500">
                    <option value="">Tất cả danh mục</option>
                </select>
                <label class="inline-flex items-center gap-2 text-sm text-gray-700">
                    <input id="filterExpiring" type="checkbox" class="rounded border-gray-300"> Sắp hết hạn (≤ 1 tháng)
                </label>
                <label class="inline-flex items-center gap-2 text-sm text-gray-700">
                    <input id="filterLowStock" type="checkbox" class="rounded border-gray-300"> Sắp hết hàng
                </label>
            </div>
        </div>

        <!-- Medicine Table -->
        <div class="bg-white rounded-lg shadow overflow-x-auto">
            <table class="w-full text-sm">
                <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                <tr>
                    <th class="px-3 py-2 text-center" style="width: 40px;"></th>
                    <th class="px-3 py-2 text-left">STT</th>
                    <th class="px-3 py-2 text-left">Tên thuốc</th>
                    <th class="px-3 py-2 text-left">Hoạt chất</th>
                    <th class="px-3 py-2 text-left">Hàm lượng</th>
                    <th class="px-3 py-2 text-left">Dạng bào chế</th>
                    <th class="px-3 py-2 text-left">Nhà SX</th>
                    <th class="px-3 py-2 text-left">Danh mục</th>
                    <th class="px-3 py-2 text-center">Tổng tồn kho</th>
                    <th class="px-3 py-2 text-center">Số lượng tối thiểu</th>
                    <th class="px-3 py-2 text-center">Cảnh báo</th>
                </tr>
                </thead>
                <tbody id="medicineTableBody" class="divide-y">
                @if(medicines != null && !medicines.isEmpty())
                    @for(vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM item : medicines)
                        <tr class="batch-row hidden"
                            data-medicine-name="${item.getMedicineName()}"
                            data-active-ingredient="${item.getActiveIngredient()}"
                            data-strength="@if(item.getStrength() != null)${item.getStrength()}@endif"
                            data-batch-code="@if(item.getBatchCode() != null)${item.getBatchCode()}@endif"
                            data-category="${item.getCategoryName()}"
                            data-variant-id="${item.getVariantId()}"
                            data-batch-id="${item.getBatchId()}"
                            data-inventory-id="@if(item.getInventoryId() != null)${item.getInventoryId()}@endif"
                            data-min-stock="@if(item.getMinStock() != null)${item.getMinStock()}@else${""}@endif"
                            data-unit="@if(item.getUnit() != null)${item.getUnit()}@endif"
                            data-quantity="${item.getQuantity()}"
                            data-expiry-date="@if(item.getExpiryDate() != null)${item.getExpiryDate().toString()}@endif"
                            data-manufacturer="${item.getManufacturer()}"
                            data-dosage-form="${item.getDosageForm()}"
                            data-expiry-category="${ item.getExpiryDate() != null && ( item.getExpiryDate().isBefore(java.time.LocalDate.now().plusWeeks(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusWeeks(1)) ) ? "week" : ( item.getExpiryDate() != null && item.getExpiryDate().isAfter(java.time.LocalDate.now().plusWeeks(1)) && ( item.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusMonths(1)) ) ? "month" : "none" ) }">
                        </tr>
                    @endfor
                @else
                    <tr><td colspan="12" class="px-3 py-6 text-center text-gray-500">Không có thuốc trong kho</td></tr>
                @endif
                </tbody>
            </table>
        </div>

        <div id="paginationContainer" class="mt-4 flex items-center justify-between bg-white p-4 rounded-lg shadow">
            <div class="text-sm text-gray-600">
                Hiển thị <span id="showingFrom">1</span> - <span id="showingTo">10</span> trong tổng số <span id="totalItems">0</span> thuốc
            </div>
            <div class="flex gap-2" id="paginationButtons"></div>
        </div>
    </div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/warehouse_inventory.js"></script>
`
)
