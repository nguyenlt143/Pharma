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
            <th>Tổng số đơn</th>
            <th>Tiền mặt</th>
            <th>Chuyển khoản</th>
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
                    url: '/pharmacist/all/shift',
                    type: 'GET',
                    dataSrc: 'data'
                },
                columns: [
                    { data: 'shiftName', title: 'Ca làm việc' },
                    { data: 'orderCount', title: 'Tổng số đơn' },
                    { data: 'cashTotal', title: 'Tiền mặt', render: $.fn.dataTable.render.number(',', '.', 0, '') },
                    { data: 'transferTotal', title: 'Chuyển khoản', render: $.fn.dataTable.render.number(',', '.', 0, '') },
                    { data: 'totalRevenue', title: 'Doanh thu', render: $.fn.dataTable.render.number(',', '.', 0, '') }
                ],
                paging: true,
                searching: true,
                ordering: true,
                pageLength: 2,
                scrollCollapse: true,
                scrollY: '65vh',
                order: [[0, 'asc']],
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
