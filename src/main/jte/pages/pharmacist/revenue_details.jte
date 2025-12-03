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
    <table id="revenueDetailTable" class="table table-striped">
        <thead>
        <tr>
            <th>Tên thuốc</th>
            <th>Đơn vị</th>
            <th>Số lô</th>
            <th>Nhà sản xuất</th>
            <th>Quốc gia</th>
            <th>Số lượng</th>
            <th>Giá</th>
            <th>Thành tiền</th>
        </tr>
        </thead>
        <tbody></tbody>
    </table>

    <script>
        $(document).ready(function () {
            const table = $('#revenueDetailTable').DataTable({
                processing: true,
                serverSide: true,
                ajax: {
                    url: '/pharmacist/all/revenue/detail',
                    type: 'GET',
                    data: function (d) {
                        d.period = new URLSearchParams(window.location.search).get("period");
                    },
                    dataSrc: 'data'
                },
                columns: [
                    { data: 'drugName', title: 'Tên thuốc' },
                    { data: 'unit', title: 'Đơn vị' },
                    { data: 'batch', title: 'Số lô' },
                    { data: 'manufacturer', title: 'Nhà sản xuất' },
                    { data: 'country', title: 'Quốc gia' },
                    { data: 'quantity', title: 'Số lượng' },
                    {
                        data: 'price',
                        title: 'Giá',
                        render: $.fn.dataTable.render.number(',', '.', 2)
                    },
                    {
                        data: 'totalAmount',
                        title: 'Thành tiền',
                        render: $.fn.dataTable.render.number(',', '.', 2)
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
