@param String period
@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/revenue_details.css">`,
    content = @`
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Chi tiết Doanh thu - ${period}</h1>
                <div class="page-actions">
                    <a href="/pharmacist/revenues" class="btn btn-secondary">← Quay lại</a>
                </div>
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

            <!-- Revenue Detail Table -->
            <div class="table-container">
                <table id="revenueDetailTable" class="display table">
                    <thead>
                        <tr>
                            <th>Ngày</th>
                            <th>Số hóa đơn</th>
                            <th>Doanh thu</th>
                            <th>Tỷ suất lợi nhuận</th>
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
        $('#revenueDetailTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/all/revenue/detail',
                type: 'GET',
                data: {
                    period: '${period}'
                }
            },
            columns: [
                {
                    data: 'date',
                    render: function(data) {
                        return new Date(data).toLocaleDateString('vi-VN');
                    }
                },
                { data: 'invoiceCount' },
                {
                    data: 'totalAmount',
                    render: function(data) {
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'profitMargin',
                    render: function(data) {
                        return data ? (data * 100).toFixed(2) + '%' : '0%';
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
