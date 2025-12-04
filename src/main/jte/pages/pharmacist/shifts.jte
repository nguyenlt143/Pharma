@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/shifts.css">`,
    content = @`
    <main class="main-content">
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
    </main>
    `,
    pageScript = @`
    <script>
    $(document).ready(function() {
        console.log('Initializing Shift DataTable...');

        $('#shiftTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/all/shift',
                type: 'GET',
                error: function(xhr, error, thrown) {
                    console.error('DataTable Ajax Error:', error, thrown);
                    console.error('Response:', xhr.responseText);
                    console.error('Status:', xhr.status);
                    alert('Lỗi khi tải dữ liệu ca làm việc: ' + error);
                },
                success: function(data) {
                    console.log('DataTable data loaded successfully:', data);
                }
            },
            columns: [
                {
                    data: 'shiftName',
                    title: 'Tên ca'
                },
                {
                    data: 'cashTotal',
                    title: 'Tiền mặt',
                    render: function(data, type, row) {
                        console.log('Rendering cashTotal:', data);
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'transferTotal',
                    title: 'Chuyển khoản',
                    render: function(data, type, row) {
                        console.log('Rendering transferTotal:', data);
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'totalRevenue',
                    title: 'Tổng doanh thu',
                    render: function(data, type, row) {
                        console.log('Rendering totalRevenue:', data);
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'orderCount',
                    title: 'Số đơn hàng',
                    render: function(data, type, row) {
                        console.log('Rendering orderCount:', data);
                        return data || 0;
                    }
                },
                {
                    data: 'shiftName',
                    title: 'Hành động',
                    orderable: false,
                    render: function(data, type, row) {
                        console.log('Rendering action for shift:', data);
                        if (data == null || data === undefined) {
                            return '<span class="text-muted">Không có dữ liệu</span>';
                        }
                        return '<a href="/pharmacist/all/shift/detail/view?shiftName=' + encodeURIComponent(data) + '" class="btn btn-sm btn-primary">Xem chi tiết</a>';
                    }
                }
            ],
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json',
                processing: 'Đang tải dữ liệu ca làm việc...',
                emptyTable: 'Không có dữ liệu ca làm việc',
                error: 'Lỗi khi tải dữ liệu ca làm việc'
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
    });
    </script>
    `
)
