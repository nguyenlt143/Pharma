@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/invoices.css">`,
    content = @`
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Danh sách Hóa đơn</h1>
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

            <!-- Invoice Table -->
            <div class="table-container">
                <table id="invoiceTable" class="display table">
                    <thead>
                        <tr>
                            <th>Mã hóa đơn</th>
                            <th>Khách hàng</th>
                            <th>Tổng tiền</th>
                            <th>Ngày tạo</th>
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
        $('#invoiceTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/invoices/all',
                type: 'GET'
            },
            order: [[3, 'desc']], // Sắp xếp theo cột "Ngày tạo" (index 3) giảm dần (mới nhất trước)
            columns: [
                { data: 'invoiceCode' },
                {
                    data: 'customerName',
                    render: function(data) {
                        return data && data.trim() !== '' ? data : 'Khách lẻ';
                    }
                },
                {
                    data: 'totalPrice',
                    render: function(data) {
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'createdAt',
                    render: function(data) {
                        return new Date(data).toLocaleString('vi-VN');
                    }
                },
                {
                    data: 'id',
                    render: function(data) {
                        return '<a href="/pharmacist/invoices/detail?invoiceId=' + data + '" class="btn btn-sm btn-primary">Xem chi tiết</a>';
                    }
                }
            ],
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json'
            }
        });
    });
    </script>
    `
)
