@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/work_schedule.css">`,
content = @`
    <!-- Main Content Area -->
    <main class="main-content">
        <div class="content-card">
            <div class="card-header">
                <h2 class="card-title">
                    <span class="material-icons title-icon">menu</span>
                    Tổng kết Ca
                </h2>
            </div>

            <div class="schedule-card">
                <div class="schedule-controls">
                    <div class="control-group">
                        <label class="control-label">YEAR</label>
                        <select class="control-select">
                            <option>2025</option>
                        </select>
                    </div>

                    <div class="control-group">
                        <label class="control-label">WEEK</label>
                        <select class="control-select week-select">
                            <option>13/10 To 19/10</option>
                        </select>
                    </div>
                </div>

                <div class="schedule-table">
                    <div class="table-header">
                        <div class="header-cell empty-cell"></div>
                        <div class="header-cell">MON<br>13/10</div>
                        <div class="header-cell">TUE<br>14/10</div>
                        <div class="header-cell">WED<br>15/10</div>
                        <div class="header-cell">THU<br>16/10</div>
                        <div class="header-cell">FRI<br>17/10</div>
                        <div class="header-cell">SAT<br>18/10</div>
                        <div class="header-cell">SUN<br>19/10</div>
                    </div>

                    <div class="table-body">
                        <div class="table-row">
                            <div class="row-label">Ca Sáng (8:00-11:00)</div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell empty-cell"></div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell empty-cell"></div>
                        </div>

                        <div class="table-row">
                            <div class="row-label">Ca Chiều (13:00- 17:00)</div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell empty-cell"></div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell empty-cell"></div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                        </div>

                        <div class="table-row">
                            <div class="row-label">Ca Tối (20:00-22:00)</div>
                            <div class="table-cell empty-cell"></div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell empty-cell"></div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell empty-cell"></div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                            <div class="table-cell">
                                <span class="material-icons check-icon">check_circle</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card-footer">
                <button class="return-button">
                    <span class="material-icons">arrow_back</span>
                    TRỞ VỀ
                </button>
            </div>
        </div>
    </main>
`,
pageScript = @` <script src="/assets/js/pharmacist/work_schedule.js" defer></script>`
)