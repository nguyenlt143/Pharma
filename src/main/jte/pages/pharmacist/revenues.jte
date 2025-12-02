@param String error = null
@param String message = null

@template.layouts.layout_test(content = @`
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

    <table id="invoiceTable" class="table table-striped">
        <thead>
        <tr>
            <th>Thời gian</th>
            <th>Số hoá đơn</th>
            <th>Số khách hàng</th>
            <th>Doanh thu</th>
        </tr>
        </thead>
        <tbody></tbody>
    </table>

    <script>
        $(document).ready(function () {
            const table = $('#invoiceTable').DataTable({
                processing: true,
                serverSide: true,
                ajax: {
                    url: '/pharmacist/all/revenue',
                    type: 'GET',
                    dataSrc: 'data'
                },
                columns: [
                    {data: 'period', title: 'Thời gian'},
                    {data: 'totalInvoice', title: 'Số hoá đơn'},
                    {data: 'totalCustomer', title: 'Số khách hàng'},
                    {data: 'totalRevenue', title: 'Doanh thu'},
                        {
                        data: 'period',
                        orderable: false,
                        searchable: false,
                        render: function(period) {
                            return '<a href="/pharmacist/all/revenue/detail?period=' + period + '" ' +
                                   'class="btn btn-sm btn-primary">' +
                                   '<i class="bi bi-eye"></i> Xem chi tiết' +
                                   '</a>';
                        }
                     }
                ],
                paging: true,
                searching: true,
                ordering: true,
                pageLength: 5,
                scrollCollapse: true,
                scrollY: '65vh',
                order: [[0, "asc"]],
                language: {
                    search: "Tìm kiếm:",
                    lengthMenu: "Hiển thị _MENU_ dòng",
                    info: "Hiển thị _START_ đến _END_ trong tổng _TOTAL_ kết quả",
                    paginate: {
                        first: "Đầu",
                        last: "Cuối",
                        next: "Sau",
                        previous: "Trước"
                    },
                    processing: "Đang tải dữ liệu..."
                }
            });
        });
    </script>
`)
