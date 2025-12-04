@param String shiftName
@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/shift_details.css">`,
    content = @`
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Chi tiết Ca làm việc - ${shiftName}</h1>
                <div class="page-actions">
                    <a href="/pharmacist/shifts" class="btn btn-secondary">← Quay lại</a>
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

            <!-- Shift Detail Table -->
            <div class="table-container">
                <table id="shiftDetailTable" class="display table">
                    <thead>
                        <tr>
                            <th>Ngày</th>
                            <th>Thời gian bắt đầu</th>
                            <th>Thời gian kết thúc</th>
                            <th>Doanh thu</th>
                            <th>Số hóa đơn</th>
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
        $('#shiftDetailTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '/pharmacist/all/shift/detail',
                type: 'GET',
                data: {
                    shiftName: '${shiftName}'
                }
            },
            columns: [
                {
                    data: 'date',
                    render: function(data) {
                        return new Date(data).toLocaleDateString('vi-VN');
                    }
                },
                {
                    data: 'startTime',
                    render: function(data) {
                        return data ? new Date(data).toLocaleTimeString('vi-VN') : 'N/A';
                    }
                },
                {
                    data: 'endTime',
                    render: function(data) {
                        return data ? new Date(data).toLocaleTimeString('vi-VN') : 'N/A';
                    }
                },
                {
                    data: 'totalAmount',
                    render: function(data) {
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                { data: 'invoiceCount' }
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
