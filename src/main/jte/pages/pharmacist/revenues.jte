@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/revenues.css">`,
    content = @`
    <main class="main-content">
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
                            <th>Số đơn hàng</th>
                            <th>Trung bình/đơn</th>
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
        $('#revenueTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/all/revenue',
                type: 'GET'
            },
            columns: [
                { data: 'period' },
                {
                    data: 'totalRevenue',
                    render: function(data) {
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                { data: 'totalOrders' },
                {
                    data: 'averagePerOrder',
                    render: function(data) {
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'period',
                    render: function(data) {
                        return '<a href="/pharmacist/all/revenue/detail/view?period=' + encodeURIComponent(data) + '" class="btn btn-sm btn-primary">Xem chi tiết</a>';
                    }
                }
            ],
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json'
            },
            order: [[0, 'desc']]
        });
    });
    </script>
    `
)
