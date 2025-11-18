document.addEventListener('DOMContentLoaded', function() {
        // Buttons filter
        const filterButtons = document.querySelectorAll('.filter-btn');

        filterButtons.forEach(button => {
            button.addEventListener('click', function() {
                filterButtons.forEach(btn => btn.classList.remove('active'));
                this.classList.add('active');

                const days = Number(this.dataset.range); // 0 / 7 / 30
                loadDashboard(days);
            });
        });

        // Initial load: today (0)
        loadDashboard(0);

        // Hover effects (stat + chart cards)
        addHoverEffect('.stat-card', -2);
        addHoverEffect('.chart-card', -1);

        async function loadDashboard(days) {
            try {
                const response = await fetch(`/api/manager/dashboard?days=${days}`);
                if (!response.ok) {
                    console.error('API error', response.status);
                    return;
                }

                const data = await response.json();
                // Truyền 'days' vào hàm updateUI
                updateDashboardUI(data, days);
            } catch (err) {
                console.error('Failed to load dashboard:', err);
            }
        }

        // safe setter for innerText to avoid errors when element missing
        function setText(id, text) {
            const el = document.getElementById(id);
            if (el) el.innerText = text;
        }

        function updateDashboardUI(data, days) {
            const daysNum = Number(days);

            // --- PHẦN THÊM MỚI ĐỂ ĐỔI NHÃN ---
            let timeLabel = "hôm nay";
            if (daysNum === 7) {
                timeLabel = "7 ngày";
            } else if (daysNum === 30) {
                timeLabel = "30 ngày";
            }

            // Cập nhật các nhãn
            setText('revenue-label', `Doanh thu ${timeLabel}`);
            setText('profit-label', `Lợi nhuận ${timeLabel}`);
            setText('orders-label', `Số đơn ${timeLabel}`);
            // --- HẾT PHẦN THÊM MỚI ---

            // Lấy KPI từ payload (data.kpis) và cập nhật UI
            const kpis = (data && data.kpis) ? data.kpis : {};
            setText('revenue', kpis.revenue ?? '0');
            setText('profit', kpis.profit ?? '0');
            setText('orders', kpis.orderCount ?? '0');

            // Nếu có change % từ API (không bắt buộc)
            setText('changeRevenue', data.changeRevenue || '');
            setText('changeProfit', data.changeProfit || '');
            setText('changeOrders', data.changeOrders || '');

            // --- XỬ LÝ HIỂN THỊ CHART THEO ĐIỀU KIỆN ---
            const revenueChartCard = document.getElementById('revenue-chart-card');
            const revenueChartTitle = document.getElementById('revenue-chart-title');

            if (daysNum === 0) {
                // Trường hợp hôm nay: ẨN đồ thị
                if (revenueChartCard) {
                    revenueChartCard.classList.add('hidden');
                }
            } else if (daysNum === 7) {
                // Trường hợp 7 ngày: Hiển thị bar chart
                if (revenueChartCard) {
                    revenueChartCard.classList.remove('hidden');
                }
                if (revenueChartTitle) {
                    revenueChartTitle.textContent = 'Doanh thu 7 ngày gần nhất';
                }
                renderBarChart(data);
            } else if (daysNum === 30) {
                // Trường hợp 30 ngày: Hiển thị line chart
                if (revenueChartCard) {
                    revenueChartCard.classList.remove('hidden');
                }
                if (revenueChartTitle) {
                    revenueChartTitle.textContent = 'Biểu đồ doanh thu 30 ngày';
                }
                renderLineChart(data);
            }

            // --- RENDER PRODUCT STATS LIST (similar to owner dashboard) ---
            const productStatsEl = document.getElementById('productStatsList');
            if (productStatsEl) {
                // Expect data.productStats = [{ name: 'Thuốc giảm đau', percent: 80, color: '#4CAF50' }, ...]
                const stats = Array.isArray(data.productStats) ? data.productStats : [];
                productStatsEl.innerHTML = '';

                if (stats.length === 0) {
                    productStatsEl.innerHTML = '<p style="color: #6B7280; text-align: center; padding: 20px;">Không có dữ liệu</p>';
                } else {
                    let html = '';
                    stats.forEach(item => {
                        html += `
                            <div class="product-item">
                                <div style="flex: 1;">
                                    <div class="product-name">${item.name || ''}</div>
                                    <div class="product-bar">
                                        <div class="product-bar-fill" style="width: ${item.percent || 0}%; background-color: ${item.color || '#4CAF50'};"></div>
                                    </div>
                                </div>
                                <div class="product-percent">${item.percent || 0}%</div>
                            </div>
                        `;
                    });
                    productStatsEl.innerHTML = html;
                }
            }
        }

        // Biến global để lưu chart instance
        let revenueLineChartInstance;

        function renderBarChart(data) {
            const revenueChart = document.getElementById('revenue-chart');
            const lineChartCanvas = document.getElementById('revenue-line-chart');

            // Hiển thị bar chart, ẩn line chart
            if (revenueChart) revenueChart.style.display = 'flex';
            if (lineChartCanvas) lineChartCanvas.style.display = 'none';

            // Destroy line chart nếu tồn tại
            if (revenueLineChartInstance) {
                revenueLineChartInstance.destroy();
                revenueLineChartInstance = null;
            }

            if (!revenueChart) return;

            // Sử dụng dailyRevenues từ backend
            const dailyRevenues = Array.isArray(data.dailyRevenues) ? data.dailyRevenues : [];

            // Clear existing
            revenueChart.innerHTML = '';

            if (dailyRevenues.length === 0) {
                revenueChart.innerHTML = '<div class="empty-chart">Không có dữ liệu</div>';
                return;
            }

            // Chuyển đổi dailyRevenues thành format cho bar chart
            const max = Math.max(...dailyRevenues.map(d => Number(d.revenue) || 0), 1);

            dailyRevenues.forEach((d, idx) => {
                const container = document.createElement('div');
                container.className = 'bar-container';

                const bar = document.createElement('div');
                bar.className = 'bar';
                const maxHeight = 228; // px
                const height = Math.round(((Number(d.revenue) || 0) / max) * maxHeight);
                bar.style.height = `${height}px`;
                bar.style.backgroundColor = '#2563EB';
                bar.style.borderRadius = '4px';

                const label = document.createElement('span');
                label.className = 'bar-label';
                if (idx === dailyRevenues.length - 1) label.classList.add('active');

                // Format date label
                const date = new Date(d.date);
                const dayLabel = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
                label.innerText = dayLabel;

                container.appendChild(bar);
                container.appendChild(label);
                revenueChart.appendChild(container);
            });
        }

        function renderLineChart(data) {
            const revenueChart = document.getElementById('revenue-chart');
            const lineChartCanvas = document.getElementById('revenue-line-chart');

            // Ẩn bar chart, hiển thị line chart
            if (revenueChart) revenueChart.style.display = 'none';
            if (lineChartCanvas) lineChartCanvas.style.display = 'block';

            if (!lineChartCanvas) return;

            // Destroy existing chart
            if (revenueLineChartInstance) {
                revenueLineChartInstance.destroy();
            }

            const dailyRevenues = Array.isArray(data.dailyRevenues) ? data.dailyRevenues : [];

            if (dailyRevenues.length === 0) {
                const ctx = lineChartCanvas.getContext('2d');
                ctx.clearRect(0, 0, lineChartCanvas.width, lineChartCanvas.height);
                ctx.font = '16px Arial';
                ctx.fillStyle = '#6B7280';
                ctx.textAlign = 'center';
                ctx.fillText('Không có dữ liệu', lineChartCanvas.width / 2, lineChartCanvas.height / 2);
                return;
            }

            const labels = dailyRevenues.map(item => {
                const date = new Date(item.date);
                return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
            });
            const revenues = dailyRevenues.map(item => item.revenue || 0);

            revenueLineChartInstance = new Chart(lineChartCanvas, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Doanh thu',
                        data: revenues,
                        borderColor: '#2563EB',
                        backgroundColor: 'rgba(37, 99, 235, 0.1)',
                        tension: 0.4,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top'
                        },
                        tooltip: {
                            mode: 'index',
                            intersect: false,
                            callbacks: {
                                label: function(context) {
                                    return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN', {
                                        style: 'currency',
                                        currency: 'VND'
                                    }).format(context.parsed.y);
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
                                }
                            }
                        }
                    }
                }
            });
        }

        function addHoverEffect(selector, translateY) {
            const cards = document.querySelectorAll(selector);
            cards.forEach(card => {
                card.addEventListener('mouseenter', () => {
                    card.style.transform = `translateY(${translateY}px)`;
                    card.style.boxShadow = '0px 4px 6px -1px rgba(0,0,0,0.1), 0px 2px 4px rgba(0,0,0,0.1)';
                });
                card.addEventListener('mouseleave', () => {
                    card.style.transform = 'translateY(0)';
                    card.style.boxShadow = '0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px rgba(0,0,0,0.1)';
                });
            });
        }
    });
