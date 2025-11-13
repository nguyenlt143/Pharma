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
            <th>Mã hoá đơn</th>
            <th>Khách hàng</th>
            <th>Ngày bán hàng</th>
            <th>Tổng tiền</th>
            <th>Phương thức thanh toán</th>
            <th></th>
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
                    url: '/pharmacist/invoices/all',
                    type: 'GET',
                    dataSrc: 'data'
                },
                columns: [
                    {data: 'invoiceCode', title: 'Mã hoá đơn'},
                    {data: 'customerName', title: 'Tên khách hàng'},
                    {
                      data: 'createdAt',
                      title: 'Ngày bán hàng',
                      render: function (data, type, row) {
                        return '<span>' + (data ? dayjs(data).format("YYYY/MM/DD HH:mm") : '') + '</span>'
                        }
                    },
                    {data: 'totalPrice', title: 'Tổng tiền'},
                    {data: 'paymentMethod', title: 'Phương thức thanh toán'},
                    {
                        data: null,
                        orderable: false,
                        searchable: false,
                        render: function (data, type, row) {
                        return '' +
                                '<button class="btn btn-sm btn-outline-primary me-2 view-btn" data-id="' + row.id + '">' +
                                '<i class="bi bi-eye"></i>Xem chi tiết</button>'
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
                    infoFiltered: "",
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

        $('#invoiceTable').on('click', '.view-btn', function () {
            const id = $(this).data('id');
            window.location.href = '/pharmacist/invoices/detail?invoiceId=' + id;
        });
    </script>
`)
