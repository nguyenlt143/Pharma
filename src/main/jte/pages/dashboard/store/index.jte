@param String error = null
@param String message = null

@template.layouts.dashboard(content = @`
    @if(error != null)
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    @if(message != null)
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    <table id="storeTable" class="table table-striped">
        <thead>
        <tr>
            <th>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="selectAll">
                    <label class="form-check-label" for="selectAll"></label>
                </div>
            </th>
            <th>Code</th>
            <th>Name</th>
            <th>Address</th>
            <th>Created At</th>
            <th>Updated At</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <!-- Update Modal -->
    <div class="modal fade" id="updateModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <form id="updateForm" method="POST" action="/dashboard/stores/update">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Update Store</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="updateId" name="id"/>
                        <div class="mb-3">
                            <label for="updateCode" class="form-label">Code</label>
                            <input type="text" id="updateCode" name="storeCode" class="form-control" required/>
                        </div>
                        <div class="mb-3">
                            <label for="updateName" class="form-label">Name</label>
                            <input type="text" id="updateName" name="storeName" class="form-control" required/>
                        </div>
                        <div class="mb-3">
                            <label for="updateAddress" class="form-label">Address</label>
                            <input type="text" id="updateAddress" name="address" class="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label for="updateProvince" class="form-label">Province</label>
                            <select id="updateProvince" name="provinceCode" class="form-select" required>
                                <option value="">-- Select Province --</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="updateWard" class="form-label">Ward</label>
                            <select id="updateWard" name="addressCode" class="form-select" required>
                                <option value="">-- Select Ward --</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            let wardsData = [];
            let wardToProvinceMap = {};

            // Load dữ liệu từ file JSON
            $.getJSON('/assets/vn_data.json', function (data) {
                wardsData = data;

                // Tạo map wardCode -> provinceCode
                wardsData.forEach(province => {
                    if (province.Wards) {
                        province.Wards.forEach(ward => {
                            wardToProvinceMap[ward.Code] = province.Code;
                        });
                    }
                });

                populateProvinces();
            });

            function populateProvinces() {
                const $province = $('#updateProvince');
                $province.empty().append('<option value="">-- Select Province --</option>');
                wardsData.forEach(province => {
                    $province.append('<option value="' + province.Code + '">' + province.FullName + '</option>');
                });
            }

            function populateWards(provinceCode, selectedWardCode = "") {
                const $ward = $('#updateWard');
                $ward.empty().append('<option value="">-- Select Ward --</option>');

                if (!provinceCode) return;

                const province = wardsData.find(p => p.Code === provinceCode);
                if (province && province.Wards) {
                    province.Wards.forEach(ward => {
                        $ward.append('<option value="' + ward.Code + '">' + ward.FullName + '</option>');
                    });
                }

                if (selectedWardCode) {
                    $ward.val(selectedWardCode);
                }
            }

            // Khi chọn province thì load wards
            $('#updateProvince').on('change', function () {
                populateWards($(this).val());
            });

            const table = $('#storeTable').DataTable({
                layout: {
                    topStart: ['search', 'pageLength'],
                    topEnd: {
                        div: {
                            className: 'dt-buttons ms-2',
                            html:
                                '<button id="deleteBtn" class="btn btn-outline-danger btn-sm me-2">' +
                                '<i class="bi bi-trash"></i> Delete Selected' +
                                '</button>' +
                                '<button id="exportBtn" class="btn btn-outline-success btn-sm me-2">' +
                                '<i class="bi bi-download"></i> Export' +
                                '</button>'

                        }
                    },
                    bottomStart: ["paging", "info"],
                    bottomEnd: null,
                },
                processing: true,
                serverSide: true,
                ajax: {
                    url: '/dashboard/stores/all',
                    type: 'GET',
                    dataSrc: 'data'
                },
                columns: [
                    {
                        data: 'id',
                        orderable: false,
                        render: function (id) {
                            return '<div class="form-check">' +
                                '<input class="form-check-input row-checkbox" type="checkbox" value="' + id + '" id="chk-' + id + '">' +
                                '<label class="form-check-label" for="chk-' + id + '"></label>' +
                                '</div>';
                        }
                    },
                    {data: 'storeCode'},
                    {data: 'storeName'},
                    {data: 'address'},
                    {
                        data: null,
                        render: function (row) {
                            return '<span>' + (row.createdAt ? dayjs(row.createdAt).format("YYYY/MM/DD HH:mm") : '') + '</span>'
                                + '<br/><span>(' + (row.createdByName || 'unknown') + ')</span>';
                        }
                    },
                    {
                        data: null,
                        render: function (row) {
                            return '<span>' + (row.updatedAt ? dayjs(row.updatedAt).format("YYYY/MM/DD HH:mm") : '') + '</span>'
                                + '<br/><span>(' + (row.updatedByName || 'unknown') + ')</span>';
                        }
                    },
                    {
                        data: null,
                        orderable: false,
                        render: function (row) {
                            return '' +
                                '<button class="btn btn-sm btn-outline-primary me-2 update-btn" data-id="' + row.id + '">' +
                                '<i class="bi bi-pencil"></i></button>' +
                                '<button class="btn btn-sm btn-outline-danger delete-btn" data-id="' + row.id + '">' +
                                '<i class="bi bi-trash"></i></button>';
                        }
                    }
                ],
                paging: true,
                searching: true,
                ordering: true,
                pageLength: 10,
                scrollCollapse: true,
                scrollY: '65vh',
                order: [[1, "asc"]] // vì cột 0 là checkbox
            });

            $('#selectAll').on('change', function () {
                $('.row-checkbox').prop('checked', this.checked);
            });

            $('#deleteBtn').on('click', function () {
                const ids = $('.row-checkbox:checked').map(function () {
                    return $(this).val();
                }).get();

                if (ids.length === 0) {
                    alert("Please select at least one store to delete.");
                    return;
                }

                if (!confirm("Are you sure you want to delete selected stores?")) {
                    return;
                }

                $.ajax({
                    url: '/dashboard/stores/delete',
                    type: 'POST',
                    data: JSON.stringify(ids),
                    contentType: 'application/json',
                    success: function () {
                        table.ajax.reload();
                    },
                    error: function () {
                        alert("Failed to delete stores.");
                    }
                });
            });

            $('#storeTable tbody').on('click', 'tr', function (e) {
                if ($(e.target).is('input[type="checkbox"]') || $(e.target).is('button')) {
                    return;
                }

                const $checkbox = $(this).find('.row-checkbox');
                $checkbox.prop('checked', !$checkbox.prop('checked'));
            });

            $('#exportBtn').on('click', function () {
                window.location.href = '/dashboard/stores/export';
            });

            $('#storeTable tbody').on('click', '.update-btn', function () {
                const rowId = $(this).data('id');
                const rowData = table.row($(this).closest('tr')).data();

                $('#updateId').val(rowData.id);
                $('#updateCode').val(rowData.storeCode);
                $('#updateName').val(rowData.storeName);
                $('#updateAddress').val(rowData.address);

                const addressCode = rowData.addressCode || "";
                const provinceCode = wardToProvinceMap[addressCode] || "";
                $('#updateProvince').val(provinceCode);
                populateWards(provinceCode, addressCode);

                $('#updateModal').modal('show');
            });

            $('#storeTable tbody').on('click', '.delete-btn', function () {
                const rowId = $(this).data('id');
                if (!confirm("Are you sure you want to delete this store?")) {
                    return;
                }

                $.ajax({
                    url: '/dashboard/stores/delete',
                    type: 'POST',
                    data: JSON.stringify([rowId]),
                    contentType: 'application/json',
                    success: function () {
                        table.ajax.reload();
                    },
                    error: function () {
                        alert("Failed to delete store.");
                    }
                });
            });
        });
    </script>
`)
