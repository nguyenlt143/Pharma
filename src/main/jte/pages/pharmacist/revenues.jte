@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/revenues.css">`,
    content = @`
    <div class="content-wrapper">
        <!-- Page Header -->
        <header class="page-header">
            <h1 class="page-title">Báo cáo Doanh thu</h1>
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

        <!-- Revenue Table -->
        <div class="table-container">
            <table id="revenueTable" class="display table">
                <thead>
                    <tr>
                        <th>Kỳ báo cáo</th>
                        <th>Doanh thu</th>
                        <th>Số hóa đơn</th>
                        <th>Số khách hàng</th>
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
        console.log('Initializing DataTable for revenues...');
        console.log('jQuery version:', $.fn.jquery);
        console.log('DataTable element exists:', $('#revenueTable').length > 0);

        $('#revenueTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/all/revenue',
                type: 'GET',
                error: function(xhr, error, thrown) {
                    console.error('DataTable Ajax Error:', error, thrown);
                    console.error('Response:', xhr.responseText);
                    alert('Lỗi khi tải dữ liệu: ' + error);
                }
            },
            columns: [
                {
                    data: 'period',
                    title: 'Kỳ báo cáo'
                },
                {
                    data: 'totalRevenue',
                    title: 'Doanh thu',
                    render: function(data) {
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'totalInvoice',
                    title: 'Số hóa đơn'
                },
                {
                    data: 'totalCustomer',
                    title: 'Số khách hàng'
                },
                {
                    data: 'period',
                    title: 'Hành động',
                    orderable: false,
                    render: function(data, type, row) {
                        console.log('Rendering action for period:', data, 'Type:', type);
                        if (data == null || data === undefined || data === '') {
                            return '<span class="text-muted">Không có dữ liệu</span>';
                        }
                        const detailUrl = '/pharmacist/all/revenue/detail/view?period=' + encodeURIComponent(data);
                        console.log('Generated URL for period "' + data + '":', detailUrl);
                        return '<button type="button" class="btn btn-sm btn-primary detail-btn" data-period="' + data + '" data-url="' + detailUrl + '">Xem chi tiết</button>';
                    }
                }
            ],
            language: {
                processing: 'Đang tải dữ liệu...',
                search: 'Tìm kiếm:',
                lengthMenu: 'Hiển thị _MENU_ bản ghi',
                info: 'Hiển thị _START_ đến _END_ của _TOTAL_ bản ghi',
                infoEmpty: 'Hiển thị 0 đến 0 của 0 bản ghi',
                infoFiltered: '(được lọc từ _MAX_ bản ghi)',
                emptyTable: 'Không có dữ liệu',
                zeroRecords: 'Không tìm thấy bản ghi nào',
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
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true,
            drawCallback: function(settings) {
                console.log('DataTable draw completed. Rows:', settings.fnRecordsDisplay());

                // Add click event handler for detail buttons
                $('.detail-btn').off('click').on('click', function(e) {
                    e.preventDefault();
                    const period = $(this).data('period');
                    const url = $(this).data('url');
                    console.log('Detail button clicked for period:', period);
                    console.log('Navigating to URL:', url);

                    if (!url) {
                        console.error('No URL found for button');
                        alert('Lỗi: Không tìm thấy URL để điều hướng');
                        return;
                    }

                    // Navigate to detail page
                    console.log('Redirecting to:', url);
                    window.location.href = url;
                });
            }
        });

        console.log('DataTable initialization completed');

        // Global event delegation for dynamically created buttons
        $(document).off('click', '.detail-btn').on('click', '.detail-btn', function(e) {
            e.preventDefault();
            console.log('Global click handler triggered');
            const period = $(this).data('period');
            const url = $(this).data('url');
            console.log('Global handler - Period:', period, 'URL:', url);

            if (!url) {
                console.error('No URL found for button');
                alert('Lỗi: Không tìm thấy URL để điều hướng');
                return;
            }

            console.log('Redirecting to:', url);
            window.location.href = url;
        });
    });
    </script>
    `
)
