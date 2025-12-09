@template.layouts.admin(
headContent = @`
    <style>
        .dashboard-container {
            width: 100%;
        }
        .welcome-card {
            background: white;
            border-radius: 8px;
            padding: 32px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            margin-bottom: 24px;
        }
        .welcome-title {
            font-size: 28px;
            font-weight: 600;
            color: #111827;
            margin-bottom: 8px;
        }
        .welcome-text {
            color: #6b7280;
            font-size: 16px;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 24px;
            margin-bottom: 24px;
        }
        .stat-card {
            background: white;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .stat-label {
            color: #6b7280;
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 8px;
        }
        .stat-value {
            color: #111827;
            font-size: 32px;
            font-weight: 600;
        }
        .quick-actions {
            background: white;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .quick-actions-title {
            font-size: 18px;
            font-weight: 600;
            color: #111827;
            margin-bottom: 16px;
        }
        .action-list {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
        .action-btn {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 16px;
            background: #f9fafb;
            border: 1px solid #e5e7eb;
            border-radius: 6px;
            color: #111827;
            text-decoration: none;
            transition: all 0.2s;
        }
        .action-btn:hover {
            background: #f3f4f6;
            border-color: #d1d5db;
        }
        .action-icon {
            font-size: 20px;
            color: #6366f1;
        }
    </style>
`,
pageScript = @``,
content = @`
    <main class="main">
        <div class="dashboard-container">
            <div class="welcome-card">
                <h1 class="welcome-title">Chào mừng đến với Quản lý Hệ thống</h1>
                <p class="welcome-text">Quản lý tài khoản Owner, Manager và Warehouse của toàn hệ thống</p>
            </div>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Tổng số Owner</div>
                    <div class="stat-value" id="ownerCount">-</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Tổng số Manager</div>
                    <div class="stat-value" id="managerCount">-</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Tổng số Warehouse</div>
                    <div class="stat-value" id="warehouseCount">-</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Tổng số Chi nhánh</div>
                    <div class="stat-value" id="branchCount">-</div>
                </div>
            </div>

            <div class="quick-actions">
                <h2 class="quick-actions-title">Thao tác nhanh</h2>
                <div class="action-list">
                    <a href="/admin/accounts" class="action-btn">
                        <span class="material-icons action-icon">manage_accounts</span>
                        <span>Quản lý tài khoản</span>
                    </a>
                    <a href="/admin/branches" class="action-btn">
                        <span class="material-icons action-icon">business</span>
                        <span>Quản lý chi nhánh</span>
                    </a>
                </div>
            </div>
        </div>
    </main>

    <script>
        // Fetch and display statistics
        document.addEventListener('DOMContentLoaded', async function() {
            try {
                // Fetch accounts
                const accountsResponse = await fetch('/api/admin/accounts?showDeleted=false');
                if (accountsResponse.ok) {
                    const accounts = await accountsResponse.json();

                    const ownerCount = accounts.filter(a => a.roleName === 'OWNER').length;
                    const managerCount = accounts.filter(a => a.roleName === 'MANAGER').length;
                    const warehouseCount = accounts.filter(a => a.roleName === 'WAREHOUSE').length;

                    document.getElementById('ownerCount').textContent = ownerCount;
                    document.getElementById('managerCount').textContent = managerCount;
                    document.getElementById('warehouseCount').textContent = warehouseCount;
                }

                // Fetch branches
                const branchesResponse = await fetch('/api/admin/accounts/branches');
                if (branchesResponse.ok) {
                    const branches = await branchesResponse.json();
                    document.getElementById('branchCount').textContent = branches.length;
                }
            } catch (error) {
                console.error('Error fetching statistics:', error);
            }
        });
    </script>
`)

