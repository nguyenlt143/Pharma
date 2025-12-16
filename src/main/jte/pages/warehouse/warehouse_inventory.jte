

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
                    <th class="px-3 py-2 text-left">STT</th>
                    <th class="px-3 py-2 text-left">Tên thuốc</th>
                    <th class="px-3 py-2 text-left">Hoạt chất</th>
                    <th class="px-3 py-2 text-left">Hàm lượng</th>
                    <th class="px-3 py-2 text-left">Dạng bào chế</th>
                    <th class="px-3 py-2 text-left">Mã lô</th>
                    <th class="px-3 py-2 text-left">HSD</th>
                    <th class="px-3 py-2 text-center">Tồn kho</th>
                    <th class="px-3 py-2 text-left">Nhà SX</th>
                    <th class="px-3 py-2 text-left">Danh mục</th>
                    <th class="px-3 py-2 text-center">Cảnh báo</th>
                </tr>
                </thead>
                <tbody id="medicineTableBody" class="divide-y">
                @if(medicines != null && !medicines.isEmpty())
                    @for(vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM item : medicines)
                        <tr class="hover:bg-gray-50 ${ (item.getExpiryDate() != null && (item.getExpiryDate().isBefore(java.time.LocalDate.now().plusWeeks(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusWeeks(1)))) ? "bg-red-50" : (item.getQuantity() != null && item.getQuantity() < 10 ? "bg-yellow-50" : "") }"
                            data-medicine-name="${item.getMedicineName()}"
                            data-active-ingredient="${item.getActiveIngredient()}"
                            data-strength="@if(item.getStrength() != null)${item.getStrength()}@endif"
                            data-batch-code="@if(item.getBatchCode() != null)${item.getBatchCode()}@endif"
                            data-category="${item.getCategoryName()}"
                            data-expiry-category="${ item.getExpiryDate() != null && ( item.getExpiryDate().isBefore(java.time.LocalDate.now().plusWeeks(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusWeeks(1)) ) ? "week" : ( item.getExpiryDate() != null && item.getExpiryDate().isAfter(java.time.LocalDate.now().plusWeeks(1)) && ( item.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusMonths(1)) ) ? "month" : "none" ) }"
                            data-low-stock="${item.getQuantity() != null && item.getQuantity() < 10}">
                            <td class="px-3 py-2">${medicines.indexOf(item)+1}</td>
                            <td class="px-3 py-2 font-medium text-blue-700">${item.getMedicineName()}</td>
                            <td class="px-3 py-2">${item.getActiveIngredient()}</td>
                            <td class="px-3 py-2">@if(item.getStrength() != null)${item.getStrength()}@else-@endif</td>
                            <td class="px-3 py-2">${item.getDosageForm()}</td>
                            <td class="px-3 py-2">@if(item.getBatchCode() != null)<span class="font-mono bg-gray-100 px-2 py-1 rounded">${item.getBatchCode()}</span>@else-@endif</td>
                            <td class="px-3 py-2">
                                @if(item.getExpiryDate() != null)
                                    @if(item.getExpiryDate().isBefore(java.time.LocalDate.now().plusWeeks(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusWeeks(1)))
                                        <span class="text-red-600 font-semibold">${item.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</span>
                                    @elseif(item.getExpiryDate().isAfter(java.time.LocalDate.now().plusWeeks(1)) && (item.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusMonths(1))))
                                        <span class="text-orange-600 font-medium">${item.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</span>
                                    @else
                                        ${item.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                    @endif
                                @else - @endif
                            </td>
                            <td class="px-3 py-2 text-center font-semibold">@if(item.getQuantity() != null)${item.getQuantity()}@else0@endif @if(item.getUnit() != null)${item.getUnit()}@endif</td>
                            <td class="px-3 py-2">${item.getManufacturer()}</td>
                            <td class="px-3 py-2">@if(item.getCategoryName() != null)<span class="text-xs bg-blue-100 px-2 py-1 rounded">${item.getCategoryName()}</span>@else-@endif</td>
                            <td class="px-3 py-2 text-center">
                                @if(item.getExpiryDate() != null && (item.getExpiryDate().isBefore(java.time.LocalDate.now().plusWeeks(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusWeeks(1))))
                                    <span class="inline-flex items-center gap-1 text-xs bg-red-100 text-red-800 px-2 py-1 rounded-full" title="Sắp hết hạn (< 1 tuần)"><span class="material-icons-outlined text-xs">warning</span>Hết hạn</span>
                                @elseif(item.getExpiryDate() != null && item.getExpiryDate().isAfter(java.time.LocalDate.now().plusWeeks(1)) && (item.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusMonths(1))))
                                    <span class="inline-flex items-center gap-1 text-xs bg-orange-100 text-orange-800 px-2 py-1 rounded-full" title="Hết hạn trong 1 tuần - 1 tháng"><span class="material-icons-outlined text-xs">error_outline</span>Sắp hết hạn</span>
                                @endif
                                @if(item.getQuantity() != null && item.getQuantity() < 10)
                                    <span class="inline-flex items-center gap-1 text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full mt-1" title="Tồn kho thấp (< 10)"><span class="material-icons-outlined text-xs">inventory_2</span>Sắp hết</span>
                                @endif
                            </td>
                        </tr>
                    @endfor
                @else
                    <tr><td colspan="11" class="px-3 py-6 text-center text-gray-500">Không có thuốc trong kho</td></tr>
                @endif
                </tbody>
            </table>
        </div>
    </div>
`,
pageScript = @`
    <script>
    const searchInputEl = document.getElementById('searchInput');
    const categoryFilterEl = document.getElementById('categoryFilter');
    const filterExpiring = document.getElementById('filterExpiring');
    const filterLowStock = document.getElementById('filterLowStock');

    function applyFilters() {
      const term = (searchInputEl.value || '').toLowerCase().trim();
      const categoryTerm = (categoryFilterEl.value || '').toLowerCase().trim();
      const wantExpiring = filterExpiring.checked;
      const wantLowStock = filterLowStock.checked;

      document.querySelectorAll('#medicineTableBody tr').forEach(row => {
        const name = (row.dataset.medicineName || '').toLowerCase();
        const active = (row.dataset.activeIngredient || '').toLowerCase();
        const batchCode = (row.dataset.batchCode || '').toLowerCase();
        const category = (row.dataset.category || '').toLowerCase();
        const expiryCat = row.dataset.expiryCategory; // week|month|none
        const lowStock = row.dataset.lowStock === 'true';

        let ok = true;
        if (term) ok = name.includes(term) || active.includes(term) || batchCode.includes(term);
        if (ok && categoryTerm) ok = category.includes(categoryTerm);
        if (ok && wantExpiring) ok = (expiryCat === 'week' || expiryCat === 'month');
        if (ok && wantLowStock) ok = lowStock;

        row.style.display = ok ? '' : 'none';
      });
    }

    searchInputEl.addEventListener('input', applyFilters);
    categoryFilterEl.addEventListener('change', applyFilters);
    filterExpiring.addEventListener('change', applyFilters);
    filterLowStock.addEventListener('change', applyFilters);

    applyFilters(); // initial
    </script>
`
)
