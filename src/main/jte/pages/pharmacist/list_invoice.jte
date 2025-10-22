@template.layouts.pharmacist(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/list_invoice.css">`,
    content = @`
    <!-- Main Content Area -->
    <main class="main-content">
        <div class="page-header">
            <h2>
                <span class="material-icons">menu</span>
                QUẢN LÝ HÓA ĐƠN
            </h2>
        </div>

        <!-- Filter Section -->
        <div class="filter-section">
            <div class="filter-row">
                <div class="filter-group">
                    <label>Từ ngày</label>
                    <div class="input-container">
                        <input type="text" value="01/08/2022" readonly>
                        <span class="material-icons">calendar_today</span>
                    </div>
                </div>

                <div class="filter-group">
                    <label>Tới ngày</label>
                    <div class="input-container">
                        <input type="text" value="10/08/2022" readonly>
                        <span class="material-icons">calendar_today</span>
                    </div>
                </div>

                <div class="filter-group">
                    <label>Loại thời gian tìm kiếm</label>
                    <select>
                        <option>Ngày bán</option>
                    </select>
                </div>

                <div class="filter-group">
                    <label>Phương thức thanh toán</label>
                    <select>
                        <option>Tất cả</option>
                    </select>
                </div>
            </div>

            <div class="filter-row">
                <div class="filter-group full-width">
                    <label>Từ khóa tìm kiếm (F3)</label>
                    <div class="search-container">
                        <input type="text" placeholder="Tìm kiếm theo mã hoá đơn, tên hàng hóa...">
                        <button class="search-button">
                            <span class="material-icons">search</span>
                            <span>TÌM KIẾM</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>


        <!-- Table Section -->
        <div class="table-section">
            <div class="table-header">
                <h3>Danh sách hóa đơn (12 Hoá đơn)</h3>
            </div>

            <div class="table-container">
                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Mã hóa đơn</th>
                        <th>Khách hàng</th>
                        <th>Ngày bán hàng</th>
                        <th class="text-right">Tổng tiền</th>
                        <th class="text-right">Thanh toán</th>
                        <th>Ghi chú</th>
                        <th>Trạng thái</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>1</td>
                        <td>
                            <div class="invoice-code">HD0000918</div>
                            <div class="invoice-type">Không bán theo đơn</div>
                        </td>
                        <td>Trần Văn A</td>
                        <td>
                            <div class="date-time">10/08/2022 09:59</div>
                            <div class="shift-info">Ca làm việc : Ca sáng</div>
                        </td>
                        <td class="text-right">5,000.00</td>
                        <td class="text-right">5,000.00</td>
                        <td></td>
                        <td>
                            <div class="status completed">
                                <span class="material-icons">check_circle</span>
                                <span>Hoàn Thành</span>
                            </div>
                        </td>
                        <td>
                            <div class="dropdown-container">
                                <button class="action-button" onclick="toggleDropdown(this)">
                                    <span>Chi tiết đơn</span>
                                    <span class="material-icons">arrow_drop_down</span>
                                </button>
                                <div class="dropdown-menu">
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">visibility</span>
                                        <span>Chi tiết phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In liều dùng</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">close</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">assignment_return</span>
                                        <span>Tạo phiếu khách trả</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">link</span>
                                        <span>Liên thông phiếu</span>
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>
                            <div class="invoice-code">HD0000917</div>
                            <div class="invoice-type">Không bán theo đơn</div>
                        </td>
                        <td>Trần Văn A</td>
                        <td>
                            <div class="date-time">10/08/2022 09:55</div>
                            <div class="shift-info">Ca làm việc : Ca sáng</div>
                        </td>
                        <td class="text-right">315,000.00</td>
                        <td class="text-right">315,000.00</td>
                        <td></td>
                        <td>
                            <div class="status completed">
                                <span class="material-icons">check_circle</span>
                                <span>Hoàn Thành</span>
                            </div>
                        </td>
                        <td>
                            <div class="dropdown-container">
                                <button class="action-button" onclick="toggleDropdown(this)">
                                    <span>Chi tiết đơn</span>
                                    <span class="material-icons">arrow_drop_down</span>
                                </button>
                                <div class="dropdown-menu">
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">visibility</span>
                                        <span>Chi tiết phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In liều dùng</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">close</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">assignment_return</span>
                                        <span>Tạo phiếu khách trả</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">link</span>
                                        <span>Liên thông phiếu</span>
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td>
                            <div class="invoice-code">HD0000916</div>
                            <div class="invoice-type">Không bán theo đơn</div>
                        </td>
                        <td>Trần Văn A</td>
                        <td>
                            <div class="date-time">10/08/2022 09:47</div>
                            <div class="shift-info">Ca làm việc : Ca sáng</div>
                        </td>
                        <td class="text-right">132,000.00</td>
                        <td class="text-right">132,000.00</td>
                        <td></td>
                        <td>
                            <div class="status completed">
                                <span class="material-icons">check_circle</span>
                                <span>Hoàn Thành</span>
                            </div>
                        </td>
                        <td>
                            <div class="dropdown-container">
                                <button class="action-button" onclick="toggleDropdown(this)">
                                    <span>Chi tiết đơn</span>
                                    <span class="material-icons">arrow_drop_down</span>
                                </button>
                                <div class="dropdown-menu">
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">visibility</span>
                                        <span>Chi tiết phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In liều dùng</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">close</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">assignment_return</span>
                                        <span>Tạo phiếu khách trả</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">link</span>
                                        <span>Liên thông phiếu</span>
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>4</td>
                        <td>
                            <div class="invoice-code">HD0000915</div>
                            <div class="invoice-type">Không bán theo đơn</div>
                        </td>
                        <td>Lý Chi Cao</td>
                        <td>
                            <div class="date-time">10/08/2022 09:25</div>
                            <div class="shift-info">Ca làm việc : Ca sáng</div>
                        </td>
                        <td class="text-right">100,000.00</td>
                        <td class="text-right">100,000.00</td>
                        <td></td>
                        <td>
                            <div class="status completed">
                                <span class="material-icons">check_circle</span>
                                <span>Hoàn Thành</span>
                            </div>
                        </td>
                        <td>
                            <div class="dropdown-container">
                                <button class="action-button" onclick="toggleDropdown(this)">
                                    <span>Chi tiết đơn</span>
                                    <span class="material-icons">arrow_drop_down</span>
                                </button>
                                <div class="dropdown-menu">
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">visibility</span>
                                        <span>Chi tiết phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In phiếu</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">print</span>
                                        <span>In liều dùng</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">close</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">assignment_return</span>
                                        <span>Tạo phiếu khách trả</span>
                                    </a>
                                    <a href="#" class="dropdown-item">
                                        <span class="material-icons">link</span>
                                        <span>Liên thông phiếu</span>
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
    `,
    pageScript = @` <script src="/assets/js/pharmacist/list_invoice.js" defer></script>`
)
