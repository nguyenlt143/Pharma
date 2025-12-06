@param String period
@param String success = null
@param String error = null

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/detail_pages_common.css">`,
    content = @`
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
                        <th>Tên thuốc</th>
                        <th>Đơn vị</th>
                        <th>Số lô</th>
                        <th>Hãng sản xuất</th>
                        <th>Xuất xứ</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                            <th>Thành tiền</th>
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
        console.log('Initializing Revenue Detail DataTable for period: ${period}');

        const table = $('#revenueDetailTable').DataTable({
            processing: true,
            serverSide: true,
            autoWidth: false,
            ajax: {
                url: '/pharmacist/all/revenue/detail',
                type: 'GET',
                data: function(d) {
                    // Add period to DataTables default parameters
                    d.period = '${period}';

                    // Enhanced sort parameter logging and validation
                    if (d.order && d.order.length > 0) {
                        const sortCol = d.order[0].column;
                        const sortDir = d.order[0].dir;
                        console.log('Sort request - Column:', sortCol, 'Direction:', sortDir);

                        // FORCE: Handle DESC client-side since server doesn't work properly
                        if (sortDir === 'desc') {
                            console.log('DESC sort detected - will handle client-side');
                            // Remove sort from server request - get unsorted data
                            delete d.order;
                            // Store client-side sort info for dataSrc
                            window.pendingClientSort = {
                                column: sortCol,
                                direction: 'desc'
                            };
                        } else {
                            // ASC works fine on server
                            window.pendingClientSort = null;
                        }

                        // Map column index to field name for debugging
                        const columnNames = ['drugName', 'unit', 'batch', 'manufacturer', 'country', 'quantity', 'price', 'totalAmount'];
                        const fieldName = columnNames[sortCol] || 'unknown';
                        console.log('Sorting field:', fieldName, 'final direction:', d.order ? d.order[0].dir : 'client-side DESC');
                    }

                    console.log('DataTables request data:', d);

                    return d;
                },
                dataSrc: function(json) {
                    console.log('Revenue Detail response:', json);
                    console.log('Data records:', json.data ? json.data.length : 0);

                    if (json.data && json.data.length > 0) {
                        console.log('First record sample:', json.data[0]);
                        console.log('Last record sample:', json.data[json.data.length - 1]);

                        // Handle client-side DESC sorting
                        if (window.pendingClientSort && window.pendingClientSort.direction === 'desc') {
                            const sortCol = window.pendingClientSort.column;
                            const columnNames = ['drugName', 'unit', 'batch', 'manufacturer', 'country', 'quantity', 'price', 'totalAmount'];
                            const fieldName = columnNames[sortCol];

                            console.log('Applying client-side DESC sort for field:', fieldName);

                            // Sort data client-side
                            json.data.sort(function(a, b) {
                                let aVal = a[fieldName];
                                let bVal = b[fieldName];

                                // Handle numeric fields
                                if (fieldName === 'quantity' || fieldName === 'price' || fieldName === 'totalAmount') {
                                    aVal = parseFloat(aVal) || 0;
                                    bVal = parseFloat(bVal) || 0;
                                    return bVal - aVal; // DESC numeric
                                }
                                // Handle text fields
                                else {
                                    aVal = (aVal || '').toString().toLowerCase();
                                    bVal = (bVal || '').toString().toLowerCase();
                                    if (bVal < aVal) return -1;
                                    if (bVal > aVal) return 1;
                                    return 0; // DESC alphabetical
                                }
                            });

                            console.log('Client-side DESC sort applied');
                            console.log('New first record:', json.data[0]);
                            console.log('New last record:', json.data[json.data.length - 1]);

                            // Clear pending sort
                            window.pendingClientSort = null;
                        }

                        // Validation for debugging
                        const currentOrder = table ? table.order() : null;
                        if (currentOrder && currentOrder.length > 0) {
                            const sortCol = currentOrder[0][0];
                            const sortDir = currentOrder[0][1];
                            const columnNames = ['drugName', 'unit', 'batch', 'manufacturer', 'country', 'quantity', 'price', 'totalAmount'];
                            const fieldName = columnNames[sortCol];

                            if (json.data.length >= 2 && fieldName) {
                                const firstVal = json.data[0][fieldName];
                                const lastVal = json.data[json.data.length - 1][fieldName];
                                console.log('Final sort validation - Field:', fieldName, 'Dir:', sortDir);
                                console.log('First value:', firstVal, 'Last value:', lastVal);

                                // Check if numeric fields are properly sorted
                                if (fieldName === 'quantity' || fieldName === 'price' || fieldName === 'totalAmount') {
                                    const firstNum = parseFloat(firstVal) || 0;
                                    const lastNum = parseFloat(lastVal) || 0;
                                    if (sortDir === 'desc' && firstNum < lastNum) {
                                        console.error('DESC sort STILL failed! First:', firstNum, 'Last:', lastNum);
                                    } else if (sortDir === 'asc' && firstNum > lastNum) {
                                        console.error('ASC sort failed! First:', firstNum, 'Last:', lastNum);
                                    } else {
                                        console.log('Sort validation passed ✓');
                                    }
                                }
                            }
                        }
                    }
                    return json.data;
                },
                error: function(xhr, error, thrown) {
                    console.error('DataTable Ajax Error:', error, thrown);
                    console.error('Response:', xhr.responseText);
                    alert('Lỗi khi tải dữ liệu chi tiết doanh thu: ' + error);
                }
            },
            columnDefs: [
                { targets: 0, width: '180px', className: 'dt-nowrap' },  // Tên thuốc - sortable
                { targets: 1, width: '80px', className: 'dt-center dt-nowrap', orderable: false },   // Đơn vị - no sort
                { targets: 2, width: '120px', className: 'dt-center dt-nowrap', orderable: false },  // Số lô - no sort
                { targets: 3, width: '150px', className: 'dt-nowrap', orderable: false },  // Hãng sản xuất - no sort
                { targets: 4, width: '100px', className: 'dt-center dt-nowrap', orderable: false },  // Xuất xứ - no sort
                { targets: 5, width: '90px', className: 'dt-center dt-nowrap' },   // Số lượng - sortable
                { targets: 6, width: '130px', className: 'dt-right dt-nowrap' },   // Đơn giá - sortable
                { targets: 7, width: '150px', className: 'dt-right dt-nowrap' }    // Thành tiền - sortable
            ],
            columns: [
                {
                    data: 'drugName',
                    render: function(data, type, row) {
                        return data || 'N/A';
                    }
                },
                {
                    data: 'unit',
                    render: function(data, type, row) {
                        return data || 'N/A';
                    }
                },
                {
                    data: 'batch',
                    render: function(data, type, row) {
                        return data || 'N/A';
                    }
                },
                {
                    data: 'manufacturer',
                    render: function(data, type, row) {
                        return data || 'N/A';
                    }
                },
                {
                    data: 'country',
                    render: function(data, type, row) {
                        return data || 'N/A';
                    }
                },
                {
                    data: 'quantity',
                    render: function(data, type, row) {
                        return data || 0;
                    }
                },
                {
                    data: 'price',
                    render: function(data, type, row) {
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                },
                {
                    data: 'totalAmount',
                    render: function(data, type, row) {
                        if (data == null || data === undefined) return '0 ₫';
                        return new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(data);
                    }
                }
            ],
            language: {
                processing: 'Đang tải dữ liệu...',
                search: 'Tìm kiếm:',
                lengthMenu: 'Hiển thị _MENU_ bản ghi',
                info: 'Hiển thị _START_ đến _END_ của _TOTAL_ bản ghi',
                infoEmpty: 'Hiển thị 0 đến 0 của 0 bản ghi',
                infoFiltered: '(được lọc từ _MAX_ bản ghi)',
                emptyTable: 'Không có dữ liệu thuốc bán trong kỳ này',
                zeroRecords: 'Không tìm thấy bản ghi nào',
                paginate: {
                    first: 'Đầu tiên',
                    last: 'Cuối cùng',
                    next: 'Tiếp theo',
                    previous: 'Trước đó'
                },
                aria: {
                    sortAscending: ': sắp xếp tăng dần',
                    sortDescending: ': sắp xếp giảm dần'
                }
            },
            order: [[0, 'asc']],
            pageLength: 25,
            scrollX: true,
            drawCallback: function(settings) {
                console.log('Revenue Detail DataTable draw completed. Rows:', settings.fnRecordsDisplay());
            },
            initComplete: function(settings, json) {
                console.log('Revenue Detail DataTable initialization completed');
            }
        });

        // Debug sort behavior with enhanced validation
        table.on('order.dt', function() {
            const order = table.order();
            console.log('Sort order changed:', order);
            console.log('Column ' + order[0][0] + ' sorted ' + order[0][1]);

            // Store current sort for validation in dataSrc
            window.currentSort = {
                column: order[0][0],
                direction: order[0][1],
                timestamp: Date.now()
            };
        });

        // Additional validation after data loads
        table.on('draw.dt', function() {
            console.log('Table redrawn after sort');

            // Validate sort after a short delay to ensure data is rendered
            setTimeout(function() {
                validateClientSideSort();
            }, 100);
        });

        // Simplified validation - just log the result
        function validateClientSideSort() {
            const order = table.order();
            if (!order || order.length === 0) return;

            const sortCol = order[0][0];
            const sortDir = order[0][1];

            console.log('Current table sort state:', 'Column', sortCol, 'Direction', sortDir);

            // Just verify the visible result - no need to fix here since dataSrc handles it
            const data = table.data().toArray();
            if (data.length >= 2) {
                const columnNames = ['drugName', 'unit', 'batch', 'manufacturer', 'country', 'quantity', 'price', 'totalAmount'];
                const fieldName = columnNames[sortCol];

                if (fieldName && (fieldName === 'quantity' || fieldName === 'price' || fieldName === 'totalAmount')) {
                    const firstVal = parseFloat(data[0][fieldName]) || 0;
                    const lastVal = parseFloat(data[data.length - 1][fieldName]) || 0;

                    console.log('Visible data check:', fieldName, 'First:', firstVal, 'Last:', lastVal);

                    if (sortDir === 'desc' && firstVal >= lastVal) {
                        console.log('✅ DESC sort is working correctly');
                    } else if (sortDir === 'asc' && firstVal <= lastVal) {
                        console.log('✅ ASC sort is working correctly');
                    } else {
                        console.log('⚠️ Sort may not be working as expected');
                    }
                }
            }
        }

        table.on('xhr.dt', function(e, settings, json, xhr) {
            console.log('Ajax request completed:', xhr.status);
            if (xhr.responseURL) {
                console.log('Request URL:', xhr.responseURL);
            }
        });

        // Fix: Sync thead and tbody width to make headers visible
        function syncTableWidths() {
            const bodyWidth = $('.dataTables_scrollBody table').outerWidth();
            const headWidth = $('.dataTables_scrollHead table').outerWidth();

            if (bodyWidth && bodyWidth > 0) {
                $('.dataTables_scrollHead table').width(bodyWidth);
                $('.dataTables_scrollHeadInner').width(bodyWidth);
                console.log('Width synced: ' + bodyWidth + 'px (was ' + headWidth + 'px)');
            }
        }

        // Sync on draw event
        table.on('draw', syncTableWidths);

        // Sync after init
        table.on('init', function() {
            setTimeout(syncTableWidths, 100);
        });

        // Force sync immediately
        setTimeout(syncTableWidths, 100);
        setTimeout(syncTableWidths, 500);
        setTimeout(syncTableWidths, 1000);

        // Sync on window resize
        $(window).on('resize', function() {
            clearTimeout(window.resizeTimer);
            window.resizeTimer = setTimeout(syncTableWidths, 250);
        });
    });
    </script>
    `
)
