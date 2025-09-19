@template.layouts.dashboard(content = @`
    <table id="userTable" class="table table-striped">
        <thead>
        <tr>
            <th>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="selectAll">
                    <label class="form-check-label" for="selectAll"></label>
                </div>
            </th>
            <th>Email</th>
            <th>Name</th>
            <th>Store</th>
            <th>Status</th>
            <th>Created At</th>
            <th>Updated At</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <script>
        $(document).ready(function () {
            const table = $('#userTable').DataTable({
                layout: {
                    topStart: ['search', 'pageLength'],
                    topEnd: {
                        div: {
                            className: 'dt-buttons ms-2',
                            html:
                                '<button id="deleteBtn" class="btn btn-outline-danger btn-sm me-2">' +
                                '<i class="bi bi-trash"></i> Delete Selected' +
                                '</button>' +
                                '<button id="exportBtn" class="btn btn-outline-success btn-sm me-2">' +
                                '<i class="bi bi-download"></i> Export' +
                                '</button>'

                        }
                    },
                    bottomStart: ["paging", "info"],
                    bottomEnd: null,
                },
                processing: true,
                serverSide: true,
                ajax: {
                    url: '/dashboard/users/all',
                    type: 'GET',
                    dataSrc: 'data'
                },
                columns: [
                    {
                        data: 'id',
                        orderable: false,
                        render: function (id) {
                            return '<div class="form-check">' +
                                '<input class="form-check-input row-checkbox" type="checkbox" value="' + id + '" id="chk-' + id + '">' +
                                '<label class="form-check-label" for="chk-' + id + '"></label>' +
                                '</div>';
                        }
                    },
                    {data: 'email'},
                    {data: 'fullName'},
                    {data: 'storeName', orderable: false},
                    {
                        data: null,
                        render: function (row) {
                            return '<span class="badge bg-success">' + row.active ? 'Active' : 'Inactive' + '</span>';
                        }
                    },
                    {
                        data: null,
                        render: function (row) {
                            return '<span>' + (row.createdAt ? dayjs(row.createdAt).format("YYYY/MM/DD HH:mm") : '') + '</span>'
                                + '<br/><span>(' + (row.createdByName || 'unknown') + ')</span>';
                        }
                    },
                    {
                        data: null,
                        render: function (row) {
                            return '<span>' + (row.updatedAt ? dayjs(row.updatedAt).format("YYYY/MM/DD HH:mm") : '') + '</span>'
                                + '<br/><span>(' + (row.updatedByName || 'unknown') + ')</span>';
                        }
                    },
                    {
                        data: null,
                        orderable: false,
                        render: function (row) {
                            return '' +
                                '<button class="btn btn-sm btn-outline-primary me-2 update-btn" data-id="' + row.id + '">' +
                                '<i class="bi bi-pencil"></i></button>' +
                                '<button class="btn btn-sm btn-outline-danger delete-btn" data-id="' + row.id + '">' +
                                '<i class="bi bi-trash"></i></button>';
                        }
                    }
                ],
                paging: true,
                searching: true,
                ordering: true,
                pageLength: 10,
                scrollCollapse: true,
                scrollY: '65vh',
                order: [[1, "asc"]] // vì cột 0 là checkbox
            });

            $('#selectAll').on('change', function () {
                $('.row-checkbox').prop('checked', this.checked);
            });

            $('#userTable tbody').on('click', 'tr', function (e) {
                if ($(e.target).is('input[type="checkbox"]') || $(e.target).is('button')) {
                    return;
                }

                const $checkbox = $(this).find('.row-checkbox');
                $checkbox.prop('checked', !$checkbox.prop('checked'));
            });
        });
    </script>
`)