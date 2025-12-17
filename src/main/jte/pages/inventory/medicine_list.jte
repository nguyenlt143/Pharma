@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines
@param Long branchId

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <title>Danh sách thuốc trong kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <style>
        #medicineTableBody tr {
            height: 60px;
        }
        #medicineTableBody td {
            vertical-align: middle;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 200px;
        }
        #medicineTableBody td:first-child,
        #medicineTableBody td:nth-child(2),
        #medicineTableBody td:nth-child(9),
        #medicineTableBody td:last-child {
            white-space: normal;
        }
    </style>
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">
    @template.layouts.inventory(active = "medicine")
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="flex justify-between items-center mb-6">
            <div>
                <h2 class="text-2xl font-bold">DANH SÁCH THUỐC TRONG KHO</h2>
                <p class="text-sm text-gray-600">Xem và lọc thuốc theo trạng thái tồn kho / hạn dùng</p>
            </div>
            <div class="text-sm text-gray-700 flex items-center gap-2">
                <span class="material-icons-outlined text-base">warehouse</span>
                Chi nhánh ${branchId} @if(medicines != null) | ${medicines.size()} loại @endif
            </div>
        </div>

        <div class="bg-white p-4 rounded-lg shadow mb-6">
            <div class="flex flex-wrap items-center gap-4">
                <div class="relative">
                    <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                    <input id="searchInput" type="text" placeholder="Tìm theo tên / hoạt chất / mã lô" maxlength="150" class="pl-10 pr-4 py-2 w-72 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
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

        <div class="bg-white rounded-lg shadow overflow-x-auto">
            <table class="w-full text-sm">
                <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                <tr>
                    <th class="px-3 py-2 text-center"><input id="selectAll" type="checkbox" class="rounded border-gray-300" title="Chọn tất cả hiển thị"></th>
                    <th class="px-3 py-2 text-left">STT</th>
                    <th class="px-3 py-2 text-left">Tên thuốc</th>
                    <th class="px-3 py-2 text-left">Hoạt chất</th>
                    <th class="px-3 py-2 text-left">Hàm lượng</th>
                    <th class="px-3 py-2 text-left">Dạng bào chế</th>
                    <th class="px-3 py-2 text-left">Mã lô</th>
                    <th class="px-3 py-2 text-left">HSD</th>
                    <th class="px-3 py-2 text-center">Tồn kho</th>
                    <th class="px-3 py-2 text-center">Số lượng tối thiểu</th>
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
                            data-variant-id="${item.getVariantId()}"
                            data-batch-id="${item.getBatchId()}"
                            data-inventory-id="@if(item.getInventoryId() != null)${item.getInventoryId()}@endif"
                            data-min-stock="@if(item.getMinStock() != null)${item.getMinStock()}@else${""}@endif"
                            data-unit="@if(item.getUnit() != null)${item.getUnit()}@endif"
                            data-available-qty="${item.getQuantity()}"
                            data-low-stock="${item.getQuantity() != null && item.getQuantity() < 10}"
                            data-expiry-category="${ item.getExpiryDate() != null && ( item.getExpiryDate().isBefore(java.time.LocalDate.now().plusWeeks(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusWeeks(1)) ) ? "week" : ( item.getExpiryDate() != null && item.getExpiryDate().isAfter(java.time.LocalDate.now().plusWeeks(1)) && ( item.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(1)) || item.getExpiryDate().isEqual(java.time.LocalDate.now().plusMonths(1)) ) ? "month" : "none" ) }">
                            <td class="px-3 py-2 text-center"><input type="checkbox" class="row-select rounded border-gray-300" aria-label="Chọn thuốc dòng ${medicines.indexOf(item)+1}"></td>
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
                            <td class="px-3 py-2 text-center">
                                <input type="number" min="0" class="min-stock-input w-24 border border-gray-300 rounded px-2 py-1 text-center" data-inventory-id="@if(item.getInventoryId() != null)${item.getInventoryId()}@endif" value="@if(item.getMinStock() != null)${item.getMinStock()}@else${""}@endif">
                            </td>
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
                    <tr><td colspan="12" class="px-3 py-6 text-center text-gray-500">Không có thuốc</td></tr>
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

        <div id="bulkActionBar" class="mt-4 p-4 border border-blue-200 bg-blue-50 rounded-lg hidden">
            <div class="flex flex-wrap items-center gap-4">
                <div class="text-sm">Đã chọn: <strong id="selectedCount" class="text-blue-600">0</strong> thuốc</div>
                <div class="ml-auto flex gap-2">
                    <button id="createImportFromList" class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md text-sm flex items-center gap-1"><span class="material-icons-outlined text-sm">inventory_2</span>Tạo phiếu nhập</button>
                    <button id="createReturnFromList" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm flex items-center gap-1"><span class="material-icons-outlined text-sm">assignment_return</span>Tạo phiếu trả</button>
                </div>
            </div>
            <p class="text-xs text-gray-500 mt-2">Chỉ chọn các dòng đang hiển thị. Số lượng sẽ lấy tối đa theo tồn kho hiện có của từng dòng.</p>
        </div>
    </main>
</div>

<script>
const searchInputEl = document.getElementById('searchInput');
const categoryFilterEl = document.getElementById('categoryFilter');
const filterExpiring = document.getElementById('filterExpiring');
const filterLowStock = document.getElementById('filterLowStock');
const selectAllCheckbox = document.getElementById('selectAll');
const bulkBar = document.getElementById('bulkActionBar');
const selectedCountSpan = document.getElementById('selectedCount');
const createImportBtn = document.getElementById('createImportFromList');
const createReturnBtn = document.getElementById('createReturnFromList');

let currentPage = 1;
const itemsPerPage = 10;

function initializeCategoryFilter() {
  const categories = new Set();
  document.querySelectorAll('#medicineTableBody tr').forEach(row => {
    // Lấy từ data-category (backend truyền sang)
    let cat = row.dataset.category ? row.dataset.category.trim() : '';
    if (cat && cat !== '-' && cat.toLowerCase() !== 'null') categories.add(cat);
    // Lấy từ text cell (nếu có)
    const catCell = row.querySelector('td:nth-child(11)');
    if (catCell) {
      const span = catCell.querySelector('.text-xs');
      let cellCat = span ? span.textContent.trim() : catCell.textContent.trim();
      if (cellCat && cellCat !== '-' && cellCat.toLowerCase() !== 'null') categories.add(cellCat);
    }
  });
  // Xóa hết option cũ trừ option đầu tiên
  while (categoryFilterEl.options.length > 1) categoryFilterEl.remove(1);
  const sorted = Array.from(categories).sort((a,b) => a.localeCompare(b, 'vi'));
  sorted.forEach(cat => {
    const opt = document.createElement('option');
    opt.value = cat.toLowerCase();
    opt.textContent = cat;
    categoryFilterEl.appendChild(opt);
  });
}

function getAllRows() {
  return Array.from(document.querySelectorAll('#medicineTableBody tr')).filter(row => {
    return row.querySelector('.row-select') !== null;
  });
}

function getVisibleRows() {
  return getAllRows().filter(row => row.style.display !== 'none');
}

function updatePagination() {
  const visibleRows = getVisibleRows();
  const totalItems = visibleRows.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
  if (currentPage < 1) currentPage = 1;

  const startIdx = (currentPage - 1) * itemsPerPage;
  const endIdx = startIdx + itemsPerPage;

  visibleRows.forEach((row, idx) => {
    if (idx >= startIdx && idx < endIdx) {
      row.classList.remove('hidden');
    } else {
      row.classList.add('hidden');
    }
  });

  document.getElementById('totalItems').textContent = totalItems;
  document.getElementById('showingFrom').textContent = totalItems > 0 ? startIdx + 1 : 0;
  document.getElementById('showingTo').textContent = Math.min(endIdx, totalItems);

  const paginationButtons = document.getElementById('paginationButtons');
  paginationButtons.innerHTML = '';

  if (totalPages <= 1) return;

  const prevBtn = document.createElement('button');
  prevBtn.innerHTML = '&laquo; Trước';
  prevBtn.className = 'px-3 py-1 border rounded-md ' + (currentPage === 1 ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-white hover:bg-gray-100');
  prevBtn.disabled = currentPage === 1;
  prevBtn.onclick = () => { if (currentPage > 1) { currentPage--; updatePagination(); } };
  paginationButtons.appendChild(prevBtn);

  const maxButtons = 5;
  let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
  let endPage = Math.min(totalPages, startPage + maxButtons - 1);

  if (endPage - startPage < maxButtons - 1) {
    startPage = Math.max(1, endPage - maxButtons + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    const pageBtn = document.createElement('button');
    pageBtn.textContent = i;
    pageBtn.className = 'px-3 py-1 border rounded-md ' + (i === currentPage ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-100');
    pageBtn.onclick = () => { currentPage = i; updatePagination(); };
    paginationButtons.appendChild(pageBtn);
  }

  const nextBtn = document.createElement('button');
  nextBtn.innerHTML = 'Sau &raquo;';
  nextBtn.className = 'px-3 py-1 border rounded-md ' + (currentPage === totalPages ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-white hover:bg-gray-100');
  nextBtn.disabled = currentPage === totalPages;
  nextBtn.onclick = () => { if (currentPage < totalPages) { currentPage++; updatePagination(); } };
  paginationButtons.appendChild(nextBtn);
}

function applyFilters() {
  const term = (searchInputEl.value || '').toLowerCase().trim();
  const categoryTerm = (categoryFilterEl.value || '').toLowerCase().trim();
  const wantExpiring = filterExpiring.checked;
  const wantLowStock = filterLowStock.checked;
  document.querySelectorAll('#medicineTableBody tr').forEach(row => {
    const checkbox = row.querySelector('.row-select');
    if (!checkbox) return;
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
  currentPage = 1;
  updatePagination();
  updateBulkBar();
}

function updateBulkBar() {
  let count = 0;
  document.querySelectorAll('#medicineTableBody tr').forEach(row => {
    if (row.style.display === 'none' || row.classList.contains('hidden')) return;
    const cb = row.querySelector('.row-select');
    if (cb && cb.checked) count++;
  });
  selectedCountSpan.textContent = count;
  bulkBar.classList.toggle('hidden', count === 0);
}

searchInputEl.addEventListener('input', applyFilters);
categoryFilterEl.addEventListener('change', applyFilters);
filterExpiring.addEventListener('change', applyFilters);
filterLowStock.addEventListener('change', applyFilters);

selectAllCheckbox.addEventListener('change', () => {
  const checked = selectAllCheckbox.checked;
  document.querySelectorAll('#medicineTableBody tr').forEach(row => {
    if (row.style.display === 'none') return;
    const cb = row.querySelector('.row-select');
    if (cb) cb.checked = checked;
  });
  updateBulkBar();
});

document.getElementById('medicineTableBody').addEventListener('change', e => {
  if (e.target.classList.contains('row-select')) updateBulkBar();
});

// Save min-stock when input changes
document.getElementById('medicineTableBody').addEventListener('change', (e) => {
  if (e.target && e.target.classList && e.target.classList.contains('min-stock-input')) {
    const input = e.target;
    const inventoryId = input.dataset.inventoryId;
    const raw = input.value;
    const value = raw === '' ? null : Number(raw);
    // POST to server
    fetch('/inventory/api/inventory/' + inventoryId + '/min-stock', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ minStock: value })
    }).then(res => res.json()).then(data => {
      if (!data || data.success !== true) {
        alert('Lưu số lượng tối thiểu thất bại: ' + (data && data.message ? data.message : ''));
      }
    }).catch(err => {
      console.error('Error saving minStock', err);
      alert('Lưu số lượng tối thiểu thất bại');
    });
  }
});

function collectSelected(forType) {
  const items = [];
  document.querySelectorAll('#medicineTableBody tr').forEach(row => {
    if (row.style.display === 'none' || row.classList.contains('hidden')) return;
    const cb = row.querySelector('.row-select');
    if (!cb || !cb.checked) return;
    const availableQty = parseInt(row.dataset.availableQty || '0');
    // Quantity defaults to maximum available for selected line
    const qty = availableQty;
    items.push({
      variantId: Number(row.dataset.variantId),
      batchId: row.dataset.batchId ? Number(row.dataset.batchId) : null,
      inventoryId: row.dataset.inventoryId ? Number(row.dataset.inventoryId) : null,
      medicineName: row.dataset.medicineName || '',
      strength: row.dataset.strength || '',
      unit: row.dataset.unit || '',
      batchCode: row.dataset.batchCode || '',
      minStock: (row.dataset.minStock && row.dataset.minStock !== '') ? Number(row.dataset.minStock) : null,
      availableQty: availableQty,
      quantity: qty
    });
  });
  if (items.length === 0) {
    alert('Chưa chọn thuốc nào.');
    return null;
  }
  const key = forType === 'import' ? 'preselectedImportItems' : 'preselectedReturnItems';
  sessionStorage.setItem(key, JSON.stringify(items));
  window.location.href = forType === 'import' ? '/inventory/import/create' : '/inventory/return/create';
}

createImportBtn.addEventListener('click', () => collectSelected('import'));
createReturnBtn.addEventListener('click', () => collectSelected('return'));

// Gọi lại initializeCategoryFilter khi trang load
initializeCategoryFilter();
applyFilters();
updatePagination();
</script>
</body>
</html>