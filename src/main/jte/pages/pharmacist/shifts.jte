@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/shifts.css">`,
    content = @`
    <div class="content-wrapper">
        <!-- Page Header -->
        <header class="page-header">
            <h1 class="page-title">Báo cáo Ca làm việc</h1>
        </header>

        <!-- Alert Messages -->
        @if(success != null)
            <div class="alert alert-success" role="alert">
                ${success}
            </div>
        @endif
        @if(error != null)
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
        @endif

        <!-- Shift Table -->
        <div class="table-container">
            <table id="shiftTable" class="display table">
                <thead>
                    <tr>
                        <th>Tên ca</th>
                        <th>Tiền mặt</th>
                        <th>Chuyển khoản</th>
                        <th>Tổng doanh thu</th>
                        <th>Số đơn hàng</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Data will be loaded via DataTables -->
                </tbody>
                </table>
            </div>
        </div>
    `,
    pageScript = @`
    <script>
    $(document).ready(function() {
        console.log('Initializing Shift DataTable...');

        const table = $('#shiftTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/all/shift',
                type: 'GET',
                dataSrc: function(json) {
                    console.log('Raw JSON response:', json);
                    console.log('Data array:', json.data);
                    console.log('Records total:', json.recordsTotal);
                    console.log('Records filtered:', json.recordsFiltered);

                    if (!json.data || json.data.length === 0) {
                        console.warn('No shift data received from server');
                    } else {
                        console.log('First row sample:', json.data[0]);
                    }

                    return json.data;
                },
                error: function(xhr, error, thrown) {
                    console.error('DataTable Ajax Error:', error, thrown);
                    console.error('Response:', xhr.responseText);
                    console.error('Status:', xhr.status);
                    alert('Lỗi khi tải dữ liệu ca làm việc: ' + error);
                }
            },
            columns: [
                {
                    data: 'shiftName',
                    render: function(data, type, row) {
                        console.log('Row data:', row);
                        return data || 'N/A';
                    }
                },
                {
                    data: 'cashTotal',
                    render: function(data, type, row) {
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'transferTotal',
                    render: function(data, type, row) {
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'totalRevenue',
                    render: function(data, type, row) {
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'orderCount',
                    render: function(data, type, row) {
                        return data || 0;
                    }
                },
                {
                    data: 'shiftName',
                    orderable: false,
                    render: function(data, type, row) {
                        if (data == null || data === undefined) {
                            return '<span class="text-muted">Không có dữ liệu</span>';
                        }
                        return '<a href="/pharmacist/all/shift/detail/view?shiftName=' + encodeURIComponent(data) + '" class="btn btn-sm btn-primary">Xem chi tiết</a>';
                    }
                }
            ],
            language: {
                processing: 'Đang tải dữ liệu ca làm việc...',
                search: 'Tìm kiếm:',
                lengthMenu: 'Hiển thị _MENU_ bản ghi',
                info: 'Hiển thị _START_ đến _END_ của _TOTAL_ bản ghi',
                infoEmpty: 'Hiển thị 0 đến 0 của 0 bản ghi',
                infoFiltered: '(được lọc từ _MAX_ bản ghi)',
                emptyTable: 'Không có dữ liệu ca làm việc',
                zeroRecords: 'Không tìm thấy ca làm việc nào',
                paginate: {
                    first: 'Đầu tiên',
                    last: 'Cuối cùng',
                    next: 'Tiếp theo',
                    previous: 'Trước đó'
                },
                aria: {
                    sortAscending: ': sắp xếp tăng dần',
                    sortDescending: ': sắp xếp giảm dần'
                }
            },
            order: [[0, 'asc']],
            pageLength: 10,
            responsive: true,
            drawCallback: function(settings) {
                console.log('Shift DataTable draw completed. Rows:', settings.fnRecordsDisplay());
            },
            initComplete: function(settings, json) {
                console.log('Shift DataTable initialization completed');
                console.log('Initial data:', json);
            }
        });

        // Fix: Sync thead and tbody width to make headers visible
        table.on('draw', function () {
            $('.dataTables_scrollHead table').width($('.dataTables_scrollBody table').width());
        });

        // Test direct API call
        $.ajax({
            url: '/pharmacist/all/shift',
            type: 'GET',
            data: {
                draw: 1,
                start: 0,
                length: 10
            },
            success: function(response) {
                console.log('Direct API test response:', response);
            },
            error: function(xhr, status, error) {
                console.error('Direct API test failed:', error);
            }
        });
    });
    </script>
    `
)
