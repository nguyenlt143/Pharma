@template.layouts.warehouse(
headContent = @`
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
    <style>
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }
        .page-title {
            font-size: 24px;
            font-weight: 700;
            color: #111827;
        }
        .btn-primary {
            background-color: #2563EB;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-primary:hover {
            background-color: #1D4ED8;
        }
        .data-table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 30px;
            border-radius: 12px;
            max-width: 600px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #374151;
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
            font-size: 14px;
        }
        .form-group input[type="checkbox"] {
            width: auto;
            margin-right: 8px;
        }
        .btn-success {
            background-color: #10B981;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
        }
        .btn-success:hover {
            background-color: #059669;
        }
        .btn-secondary {
            background-color: #6B7280;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            margin-left: 10px;
        }
        .btn-secondary:hover {
            background-color: #4B5563;
        }
        .badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }
        .badge-success {
            background-color: #D1FAE5;
            color: #065F46;
        }
        .badge-secondary {
            background-color: #E5E7EB;
            color: #374151;
        }
        .unit-checkbox-item {
            padding: 8px 12px;
            margin-bottom: 6px;
            border-radius: 6px;
            cursor: pointer;
            transition: background-color 0.2s;
            display: flex;
            align-items: center;
        }
        .unit-checkbox-item:hover {
            background-color: #E5E7EB;
        }
        .unit-checkbox-item input[type="checkbox"] {
            margin-right: 10px;
            cursor: pointer;
            width: 18px;
            height: 18px;
        }
        .unit-checkbox-item label {
            margin: 0;
            cursor: pointer;
            flex: 1;
            font-weight: 400;
        }
    </style>
`,
content = @`
    <div class="page-header">
        <h1 class="page-title">Quản lý Đơn vị theo Dạng bào chế</h1>
        <div>
            <button class="btn-primary" onclick="openManageUnitsModal()" style="margin-right: 10px;">
                <i class="fas fa-cog"></i> Quản lý đơn vị
            </button>
            <button class="btn-primary" onclick="refreshTable()">
                <i class="fas fa-sync"></i> Làm mới
            </button>
        </div>
    </div>

    <div class="data-table-container">
        <table id="dosageFormTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>Dạng bào chế</th>
                    <th>Đơn vị cơ bản</th>
                    <th>Đơn vị quy đổi khả dụng</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>

    <!-- Edit Modal -->
    <div id="dosageFormModal" class="modal">
        <div class="modal-content">
            <h2 id="modalTitle">Cấu hình đơn vị quy đổi</h2>
            <form id="dosageFormForm">
                <input type="hidden" id="dosageFormEnum">

                <div class="form-group">
                    <label>Dạng bào chế</label>
                    <input type="text" id="dosageFormDisplay" readonly style="background: #f3f4f6;">
                </div>

                <div class="form-group">
                    <label>Đơn vị cơ bản (Base Unit)</label>
                    <input type="text" id="baseUnitName" readonly style="background: #f3f4f6;">
                    <small style="color: #6B7280;">Đơn vị cơ bản được xác định tự động theo dạng bào chế</small>
                </div>

                <div class="form-group">
                    <label>Đơn vị quy đổi khả dụng *</label>
                    <div style="margin-bottom: 10px;">
                        <button type="button" class="btn btn-sm btn-secondary" onclick="selectAllUnits()" style="padding: 5px 10px; margin-right: 5px;">
                            ☑ Chọn tất cả
                        </button>
                        <button type="button" class="btn btn-sm btn-secondary" onclick="deselectAllUnits()" style="padding: 5px 10px;">
                            ☐ Bỏ chọn tất cả
                        </button>
                    </div>
                    <div id="conversionUnitsCheckboxes" style="max-height: 300px; overflow-y: auto; border: 1px solid #D1D5DB; border-radius: 6px; padding: 12px; background: #F9FAFB;">
                        <!-- Checkboxes will be populated dynamically -->
                    </div>
                    <small style="color: #6B7280;">Chọn các đơn vị có thể dùng để quy đổi từ đơn vị cơ bản.</small>
                </div>

                <div style="margin-top: 24px;">
                    <button type="submit" class="btn-success">Lưu cấu hình</button>
                    <button type="button" class="btn-secondary" onclick="closeModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Manage Units Modal -->
    <div id="manageUnitsModal" class="modal">
        <div class="modal-content" style="max-width: 900px;">
            <h2>Quản lý Đơn vị</h2>

            <div style="margin-bottom: 20px;">
                <button class="btn-success" onclick="openAddUnitModal()">
                    <i class="fas fa-plus"></i> Thêm đơn vị mới
                </button>
            </div>

            <div class="data-table-container">
                <table id="unitsManageTable" class="display" style="width:100%">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên đơn vị</th>
                            <th>Mô tả</th>
                            <th>Là Base Unit</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>

            <div style="margin-top: 20px;">
                <button type="button" class="btn-secondary" onclick="closeManageUnitsModal()">Đóng</button>
            </div>
        </div>
    </div>

    <!-- Add/Edit Unit Modal -->
    <div id="addEditUnitModal" class="modal">
        <div class="modal-content">
            <h2 id="unitModalTitle">Thêm đơn vị mới</h2>
            <form id="unitForm">
                <input type="hidden" id="unitId">

                <div class="form-group">
                    <label for="unitName">Tên đơn vị *</label>
                    <input type="text" id="unitName" required>
                </div>

                <div class="form-group">
                    <label for="unitDescription">Mô tả</label>
                    <textarea id="unitDescription" rows="3"></textarea>
                </div>

                <div class="form-group">
                    <label>
                        <input type="checkbox" id="unitIsBase">
                        Là đơn vị cơ bản (Base Unit)
                    </label>
                    <small style="display: block; color: #6B7280; margin-top: 5px;">
                        Chỉ check nếu đơn vị này là đơn vị nhỏ nhất cho một dạng bào chế
                    </small>
                </div>

                <div style="margin-top: 24px;">
                    <button type="submit" class="btn-success">Lưu</button>
                    <button type="button" class="btn-secondary" onclick="closeAddEditUnitModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        let table;
        let allUnits = {}; // Cache for all units {id: name}
        let baseUnitIds = {}; // Cache for base unit IDs {name: id}
        let baseUnitsData = {}; // Full base unit data {name: unitObject}
        let dosageForms = []; // All dosage forms from enum

        $(document).ready(function() {
            // Load all data sequentially
            loadAllData();
        });

        function loadAllData() {
            // Step 1: Load all units
            $.get('/api/warehouse/unit/all', function(units) {
                // Build cache of id -> name and name -> id
                units.forEach(function(unit) {
                    allUnits[unit.id] = unit.name;
                    if(unit.isBase) {
                        baseUnitIds[unit.name] = unit.id;
                        baseUnitsData[unit.name] = unit;
                    }
                });

                // Step 2: Load dosage forms
                $.get('/api/warehouse/unit/dosage-forms', function(forms) {
                    dosageForms = forms;
                    initializeTable();
                }).fail(function() {
                    alert('Không thể tải danh sách dạng bào chế');
                });
            }).fail(function() {
                console.error('Failed to load units');
                alert('Không thể tải danh sách đơn vị');
            });
        }

        function initializeTable() {
            table = $('#dosageFormTable').DataTable({
                data: dosageForms,
                columns: [
                    {
                        data: 'displayName',
                        render: function(data, type, row) {
                            return '<strong>' + data + '</strong>';
                        }
                    },
                    {
                        data: 'baseUnit',
                        render: function(data) {
                            return '<span class="badge badge-success">' + data + '</span>';
                        }
                    },
                    {
                        data: 'baseUnit',
                        render: function(data, type, row) {
                            // Get base unit data
                            const baseUnit = baseUnitsData[data];
                            if(!baseUnit) {
                                return '<span style="color: #9CA3AF;">Chưa tạo đơn vị</span>';
                            }

                            // Display conversion units directly
                            if(baseUnit.availableUnitIds && baseUnit.availableUnitIds.length > 0) {
                                const names = baseUnit.availableUnitIds.map(function(id) {
                                    return allUnits[id] || 'ID:' + id;
                                }).join(', ');
                                return '<span style="color: #059669;">' + names + '</span>';
                            } else {
                                return '<span style="color: #F59E0B;">Chưa cấu hình</span>';
                            }
                        }
                    },
                    {
                        data: null,
                        orderable: false,
                        render: function(data, type, row) {
                            return '<button onclick="editDosageForm(\'' + row.name + '\', \'' + row.displayName + '\', \'' + row.baseUnit + '\')" class="btn btn-sm btn-primary">' +
                                   '<i class="fas fa-edit"></i> Cấu hình' +
                                   '</button>';
                        }
                    }
                ],
                paging: false,
                searching: true,
                info: false,
                language: {
                    search: "Tìm kiếm:",
                    zeroRecords: "Không tìm thấy dạng bào chế nào",
                    emptyTable: "Không có dữ liệu"
                }
            });
        }

        function editDosageForm(enumName, displayName, baseUnitName) {
            $('#modalTitle').text('Cấu hình: ' + displayName);
            $('#dosageFormEnum').val(enumName);
            $('#dosageFormDisplay').val(displayName);
            $('#baseUnitName').val(baseUnitName);

            // Get base unit ID
            const baseUnitId = baseUnitIds[baseUnitName];

            if(!baseUnitId) {
                alert('Không tìm thấy đơn vị cơ bản: ' + baseUnitName + '. Vui lòng tạo đơn vị này trước.');
                return;
            }

            // Populate conversion units checkboxes (exclude base unit itself)
            const container = $('#conversionUnitsCheckboxes');
            container.empty();

            Object.keys(allUnits).forEach(function(id) {
                if(id != baseUnitId) { // Don't include base unit in conversion options
                    const checkboxItem = $('<div class="unit-checkbox-item"></div>');
                    const checkbox = $('<input type="checkbox" class="unit-checkbox" value="' + id + '" id="unit_' + id + '">');
                    const label = $('<label for="unit_' + id + '">' + allUnits[id] + '</label>');

                    checkboxItem.append(checkbox).append(label);
                    container.append(checkboxItem);

                    // Make entire item clickable
                    checkboxItem.on('click', function(e) {
                        if(e.target.tagName !== 'INPUT') {
                            checkbox.prop('checked', !checkbox.prop('checked'));
                        }
                    });
                }
            });

            // Load current configuration and check appropriate boxes
            $.get('/api/warehouse/unit/' + baseUnitId, function(baseUnit) {
                if(baseUnit.availableUnitIds && baseUnit.availableUnitIds.length > 0) {
                    baseUnit.availableUnitIds.forEach(function(id) {
                        $('#unit_' + id).prop('checked', true);
                    });
                }
                $('#dosageFormModal').show();
            }).fail(function() {
                alert('Không thể tải cấu hình hiện tại');
            });
        }

        function selectAllUnits() {
            $('.unit-checkbox').prop('checked', true);
        }

        function deselectAllUnits() {
            $('.unit-checkbox').prop('checked', false);
        }

        function saveDosageFormConfig() {
            const baseUnitName = $('#baseUnitName').val();
            const baseUnitId = baseUnitIds[baseUnitName];

            if(!baseUnitId) {
                alert('Không tìm thấy đơn vị cơ bản');
                return;
            }

            // Collect checked checkbox values
            const selectedIds = [];
            $('.unit-checkbox:checked').each(function() {
                selectedIds.push($(this).val());
            });
            const listUnitAvailable = selectedIds.length > 0 ? selectedIds.join(',') : '';

            const data = {
                name: baseUnitName,
                description: 'Đơn vị cơ bản cho ' + $('#dosageFormDisplay').val(),
                isBase: true,
                listUnitAvailable: listUnitAvailable
            };

            $.ajax({
                url: '/api/warehouse/unit/' + baseUnitId,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function() {
                    closeModal();
                    refreshTable();
                    alert('Cập nhật cấu hình thành công!');
                },
                error: function(xhr) {
                    alert('Có lỗi xảy ra: ' + (xhr.responseText || 'Vui lòng thử lại'));
                }
            });
        }

        function refreshTable() {
            // Reload all data and refresh table
            if(table) {
                table.destroy();
            }
            loadAllData();
        }

        function closeModal() {
            $('#dosageFormModal').hide();
            $('#dosageFormForm')[0].reset();
        }

        // ========== Manage Units Functions ==========
        let unitsTable;

        function openManageUnitsModal() {
            $('#manageUnitsModal').show();
            loadUnitsTable();
        }

        function closeManageUnitsModal() {
            if(unitsTable) {
                unitsTable.destroy();
                unitsTable = null;
            }
            $('#manageUnitsModal').hide();
        }

        function loadUnitsTable() {
            if(unitsTable) {
                unitsTable.destroy();
            }

            unitsTable = $('#unitsManageTable').DataTable({
                ajax: {
                    url: '/api/warehouse/unit',
                    type: 'GET',
                    data: function(d) {
                        return {
                            draw: d.draw,
                            start: d.start,
                            length: d.length,
                            orderColumn: 'name',
                            orderDir: 'asc',
                            'search[value]': d.search.value
                        };
                    }
                },
                columns: [
                    { data: 'id' },
                    { data: 'name' },
                    { data: 'description' },
                    {
                        data: 'isBase',
                        render: function(data) {
                            return data ? '<span class="badge badge-success">Có</span>' :
                                        '<span class="badge badge-secondary">Không</span>';
                        }
                    },
                    {
                        data: null,
                        orderable: false,
                        render: function(data, type, row) {
                            return '<button onclick="editUnit(' + row.id + ')" class="btn btn-sm btn-primary" style="margin-right: 5px;">' +
                                   '<i class="fas fa-edit"></i>' +
                                   '</button>' +
                                   '<button onclick="deleteUnit(' + row.id + ', \'' + row.name + '\')" class="btn btn-sm" style="background: #EF4444; color: white;">' +
                                   '<i class="fas fa-trash"></i>' +
                                   '</button>';
                        }
                    }
                ],
                language: {
                    url: '/assets/datatable_vi.json'
                }
            });
        }

        function openAddUnitModal() {
            $('#unitModalTitle').text('Thêm đơn vị mới');
            $('#unitId').val('');
            $('#unitName').val('');
            $('#unitDescription').val('');
            $('#unitIsBase').prop('checked', false);
            $('#addEditUnitModal').show();
        }

        function editUnit(id) {
            $.get('/api/warehouse/unit/' + id, function(unit) {
                $('#unitModalTitle').text('Sửa đơn vị');
                $('#unitId').val(unit.id);
                $('#unitName').val(unit.name);
                $('#unitDescription').val(unit.description);
                $('#unitIsBase').prop('checked', unit.isBase);
                $('#addEditUnitModal').show();
            }).fail(function() {
                alert('Không thể tải thông tin đơn vị');
            });
        }

        function deleteUnit(id, name) {
            if(confirm('Bạn có chắc chắn muốn xóa đơn vị "' + name + '"?\n\nLưu ý: Không thể xóa nếu đơn vị đang được sử dụng.')) {
                $.ajax({
                    url: '/api/warehouse/unit/' + id,
                    type: 'DELETE',
                    success: function() {
                        alert('Xóa đơn vị thành công!');
                        loadUnitsTable();
                        // Refresh main table to show updated data
                        refreshTable();
                    },
                    error: function(xhr) {
                        alert('Không thể xóa đơn vị này.\n\n' +
                              (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message :
                               'Đơn vị có thể đang được sử dụng trong các chuyển đổi.'));
                    }
                });
            }
        }

        function closeAddEditUnitModal() {
            $('#addEditUnitModal').hide();
            $('#unitForm')[0].reset();
        }

        function saveUnit() {
            const unitId = $('#unitId').val();
            const data = {
                name: $('#unitName').val(),
                description: $('#unitDescription').val(),
                isBase: $('#unitIsBase').is(':checked'),
                listUnitAvailable: null // Will be configured later via dosage form config
            };

            const url = unitId ? '/api/warehouse/unit/' + unitId : '/api/warehouse/unit';
            const method = unitId ? 'PUT' : 'POST';

            $.ajax({
                url: url,
                type: method,
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function() {
                    alert(unitId ? 'Cập nhật đơn vị thành công!' : 'Thêm đơn vị mới thành công!');
                    closeAddEditUnitModal();
                    loadUnitsTable();
                    // Refresh main table and cache
                    refreshTable();
                },
                error: function(xhr) {
                    alert('Có lỗi xảy ra: ' + (xhr.responseText || 'Vui lòng thử lại'));
                }
            });
        }

        // Form submissions
        $(document).on('submit', '#unitForm', function(e) {
            e.preventDefault();
            saveUnit();
        });

        // Form submission
        $(document).on('submit', '#dosageFormForm', function(e) {
            e.preventDefault();
            saveDosageFormConfig();
        });

        // Close modal when clicking outside
        window.onclick = function(event) {
            if (event.target == document.getElementById('dosageFormModal')) {
                closeModal();
            }
            if (event.target == document.getElementById('manageUnitsModal')) {
                closeManageUnitsModal();
            }
            if (event.target == document.getElementById('addEditUnitModal')) {
                closeAddEditUnitModal();
            }
        }
    </script>
`,
pageScript = @`
    <!-- Additional page scripts if needed -->
`
)

