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

            // --- RENDER DYNAMIC REVENUE BAR CHART ---
            const revenueChart = document.getElementById('revenue-chart');
            if (revenueChart) {
                // Expect data.revenueSeries = [{ label: 'T2', value: 123 }, ...]
                const series = Array.isArray(data.revenueSeries) ? data.revenueSeries : [];

                // Clear existing
                revenueChart.innerHTML = '';

                if (series.length === 0) {
                    revenueChart.innerHTML = '<div class="empty-chart">Không có dữ liệu</div>';
                } else {
                    // compute max for scaling
                    const max = Math.max(...series.map(s => Number(s.value) || 0), 1);

                    series.forEach((s, idx) => {
                        const container = document.createElement('div');
                        container.className = 'bar-container';

                        const bar = document.createElement('div');
                        bar.className = 'bar';
                        // scale height to a baseline (maxHeight 228 like original)
                        const maxHeight = 228; // px
                        const height = Math.round(((Number(s.value) || 0) / max) * maxHeight);
                        bar.style.height = `${height}px`;

                        // optional: colored fill using an <img> fallback like original design
                        const img = document.createElement('img');
                        img.className = 'bar-fill';
                        img.alt = 'Bar';
                        // choose one of original placeholder images or a neutral one
                        img.src = 'https://static.codia.ai/image/2025-10-26/so8mVbqdGm.png';

                        bar.appendChild(img);

                        const label = document.createElement('span');
                        label.className = 'bar-label';
                        if (idx === series.length - 1) label.classList.add('active');
                        label.innerText = s.label || '';

                        container.appendChild(bar);
                        container.appendChild(label);
                        revenueChart.appendChild(container);
                    });
                }
            }

            // --- RENDER TOP GROUPS PROGRESS LIST ---
            const topGroupsEl = document.getElementById('top-groups');
            if (topGroupsEl) {
                // Expect data.topGroups = [{ name: 'Thuốc giảm đau', percent: 80 }, ...]
                const groups = Array.isArray(data.topGroups) ? data.topGroups : [];
                topGroupsEl.innerHTML = '';

                if (groups.length === 0) {
                    topGroupsEl.innerHTML = '<div class="empty-list">Không có dữ liệu</div>';
                } else {
                    // ensure percent values normalized
                    const maxPercent = Math.max(...groups.map(g => Number(g.percent) || 0), 1);

                    groups.forEach(g => {
                        const item = document.createElement('div');
                        item.className = 'progress-item';

                        const label = document.createElement('span');
                        label.className = 'progress-label';
                        label.innerText = g.name || '';

                        const barWrap = document.createElement('div');
                        barWrap.className = 'progress-bar';

                        const fill = document.createElement('div');
                        // for consistency with CSS that may expect an <img>, we use a div with background
                        fill.className = 'progress-fill-div';
                        const percent = Math.round((Number(g.percent) || 0));
                        fill.style.width = Math.min(100, Math.max(0, percent)) + '%';

                        // optional aria
                        fill.setAttribute('aria-valuenow', String(percent));
                        fill.setAttribute('aria-valuemin', '0');
                        fill.setAttribute('aria-valuemax', '100');

                        barWrap.appendChild(fill);

                        item.appendChild(label);
                        item.appendChild(barWrap);

                        topGroupsEl.appendChild(item);
                    });
                }
            }
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
