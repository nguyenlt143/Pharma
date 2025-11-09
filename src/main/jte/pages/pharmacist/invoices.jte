@param String error = null
@param String message = null

@template.layouts.main(content = @`
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
                             + '<br/><span>(' + (row.createdByName || 'unknown') + ')</span>';
                        }
                    },
                    {data: 'totalPrice', title: 'Tổng tiền'},
                    {data: 'paymentMethod', title: 'Phương thức thanh toán'}
                ],
                paging: true,
                searching: true,
                ordering: true,
                pageLength: 5,
                scrollCollapse: true,
                scrollY: '65vh',
                order: [[0, "asc"]]
            });
        });
    </script>
`)
