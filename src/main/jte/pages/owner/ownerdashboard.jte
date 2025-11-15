<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard & Báo cáo</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="/assets/css/owner/layout.css" />
</head>

<body class="font-[Inter] bg-gray-50 text-gray-800">
<div class="flex min-h-screen">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="flex flex-col h-full justify-between">
            <div>
                <div class="p-4 border-b border-gray-700">
                    <h1 class="text-lg font-semibold text-white">PharmacyS</h1>
                    <p class="text-sm text-gray-300">Quản trị viên</p>
                </div>
                <nav class="mt-4 flex flex-col">
                    <a href="#" class="nav-item">🏠 Quản lý thuốc</a>
                    <a href="#" class="nav-item">👥 Bảng giá</a>
                    <a href="#" class="nav-item">🚚 Nhà cung cấp</a>
                    <a href="#" class="nav-item">📦 Danh mục</a>
                    <a href="#" class="nav-item">📊 Báo cáo</a>
                    <a href="#" class="nav-item active">📈 Dashboard & Hao hụt</a>
                </nav>
            </div>

            <div class="border-t border-gray-700 p-4 flex flex-col gap-2">
                <a href="#" class="nav-item">⚙️ Cài đặt</a>
                <a href="#" class="nav-item">↩️ Đăng xuất</a>
            </div>
        </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 p-8 overflow-y-auto">
        <div class="max-w-6xl mx-auto space-y-12">

            <!-- ===== DASHBOARD ===== -->
            <section>
                <h1 class="text-2xl font-bold mb-6">📈 Tổng quan doanh thu</h1>

                <!-- Cards -->
                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
                    <div class="bg-white rounded-lg shadow p-4 text-center">
                        <p class="text-gray-500 text-sm">Doanh thu hôm nay</p>
                        <p class="text-2xl font-bold text-green-600">12.500.000₫</p>
                    </div>
                    <div class="bg-white rounded-lg shadow p-4 text-center">
                        <p class="text-gray-500 text-sm">Doanh thu tháng này</p>
                        <p class="text-2xl font-bold text-blue-600">320.000.000₫</p>
                    </div>
                    <div class="bg-white rounded-lg shadow p-4 text-center">
                        <p class="text-gray-500 text-sm">Tổng đơn hàng</p>
                        <p class="text-2xl font-bold text-indigo-600">245</p>
                    </div>
                </div>

                <!-- Biểu đồ -->
                <div class="bg-white rounded-lg shadow p-6">
                    <canvas id="revenueChart" height="120"></canvas>
                </div>
            </section>

            <!-- ===== BÁO CÁO HAO HỤT ===== -->
            <section>
                <h1 class="text-2xl font-bold mb-6">💸 Báo cáo hao hụt thuốc</h1>

                <!-- Bộ lọc -->
                <div class="flex items-center gap-4 mb-4">
                    <select id="monthFilter" class="border border-gray-300 rounded-lg p-2">
                        <option value="">Chọn tháng</option>
                        <option value="10">Tháng 10</option>
                        <option value="11" selected>Tháng 11</option>
                        <option value="12">Tháng 12</option>
                    </select>
                    <select id="yearFilter" class="border border-gray-300 rounded-lg p-2">
                        <option value="2025" selected>2025</option>
                        <option value="2024">2024</option>
                    </select>
                    <button class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                        Lọc báo cáo
                    </button>
                </div>

                <!-- Bảng hao hụt -->
                <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                    <table class="w-full text-left border-collapse">
                        <thead class="bg-gray-100">
                        <tr>
                            <th class="py-3 px-4 font-semibold text-gray-700">Mã thuốc</th>
                            <th class="py-3 px-4 font-semibold text-gray-700">Tên thuốc</th>
                            <th class="py-3 px-4 font-semibold text-gray-700">Số lượng hao hụt</th>
                            <th class="py-3 px-4 font-semibold text-gray-700">Lý do</th>
                            <th class="py-3 px-4 font-semibold text-gray-700">Ngày ghi nhận</th>
                        </tr>
                        </thead>
                        <tbody id="lossTableBody">
                        <tr class="border-t hover:bg-gray-50">
                            <td class="py-3 px-4">PNC001</td>
                            <td class="py-3 px-4">Paracetamol 500mg</td>
                            <td class="py-3 px-4">5</td>
                            <td class="py-3 px-4">Hết hạn sử dụng</td>
                            <td class="py-3 px-4">2025-11-01</td>
                        </tr>
                        <tr class="border-t hover:bg-gray-50">
                            <td class="py-3 px-4">AMX002</td>
                            <td class="py-3 px-4">Amoxicillin 250mg</td>
                            <td class="py-3 px-4">2</td>
                            <td class="py-3 px-4">Hỏng bao bì</td>
                            <td class="py-3 px-4">2025-10-20</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Biểu đồ doanh thu
    new Chart(document.getElementById('revenueChart'), {
        type: 'line',
        data: {
            labels: ['Th1', 'Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8', 'Th9', 'Th10', 'Th11'],
            datasets: [{
                label: 'Doanh thu (triệu đồng)',
                data: [120, 150, 180, 250, 220, 300, 310, 280, 330, 400, 370],
                borderColor: 'rgb(37,99,235)',
                backgroundColor: 'rgba(37,99,235,0.15)',
                fill: true,
                tension: 0.25
            }]
        },
        options: {
            responsive: true,
            scales: { y: { beginAtZero: true } },
            plugins: { legend: { position: 'bottom' } }
        }
    });
</script>
</body>
</html>
