@import java.time.LocalDate
@import java.time.format.DateTimeFormatter
@param java.util.List<vn.edu.fpt.pharma.dto.shifts.ShiftSummaryVM> summaries
@param java.time.LocalDate start
@param java.time.LocalDate end

@template.layouts.layout_test(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/work_schedule.css">`,
content = @`
    <!-- Main Content Area -->
    <div class="content-card">
        <div class="card-header">
            <h2 class="card-title">
                <span class="material-icons title-icon">menu</span>
                Lịch làm việc
            </h2>
        </div>

        <div class="schedule-card">
                <div class="schedule-controls">
                    <div class="control-group">
                        <label class="control-label">Năm</label>
                        <select class="control-select">
                            <option>${start.getYear()}</option>
                        </select>
                    </div>

                    <div class="control-group">
                        <label class="control-label">Tuần</label>
                        <select class="control-select week-select">
                            <option>
                                ${start.format(DateTimeFormatter.ofPattern("dd/MM"))}
                                To
                                ${end.format(DateTimeFormatter.ofPattern("dd/MM"))}
                            </option>
                        </select>
                    </div>
                </div>

                <div class="schedule-table">
                    <div class="table-header">
                        <div class="header-cell empty-cell"></div>
                        @for(LocalDate d = start; !d.isAfter(end); d = d.plusDays(1))
                            !{
                                String dayName = switch(d.getDayOfWeek().getValue()) {
                                    case 1 -> "Thứ Hai";
                                    case 2 -> "Thứ Ba";
                                    case 3 -> "Thứ Tư";
                                    case 4 -> "Thứ Năm";
                                    case 5 -> "Thứ Sáu";
                                    case 6 -> "Thứ Bảy";
                                    case 7 -> "Chủ Nhật";
                                    default -> "";
                                };
                            }
                            <div class="header-cell">
                                <span class="day-name">${dayName}</span>
                                <span class="day-date">${d.format(DateTimeFormatter.ofPattern("dd/MM"))}</span>
                            </div>
                        @endfor
                    </div>

                    <div class="table-body">
                        @for(int i = 0; i < summaries.size(); i++)
                            <div class="table-row">
                                <div class="row-label">${summaries.get(i).shiftName()}</div>

                                @for(LocalDate d = start; !d.isAfter(end); d = d.plusDays(1))
                                    !{
                                        Boolean hasShift = summaries.get(i).days().get(d);
                                    }
                                    <div class="table-cell">
                                        @if(hasShift == null)
                                            <span class="material-icons status-icon unknown-icon" title="Không có dữ liệu">help_outline</span>
                                        @elseif(hasShift)
                                            <span class="material-icons status-icon check-icon" title="Có ca làm việc">check_circle</span>
                                        @else
                                            <span class="material-icons status-icon no-shift-icon" title="Không có ca">remove_circle</span>
                                        @endif
                                    </div>
                                @endfor
                            </div>
                        @endfor
                    </div>

                    <!-- Status Legend -->
                    <div class="status-legend">
                        <div class="legend-item">
                            <span class="material-icons status-icon check-icon">check_circle</span>
                            <span>Có ca làm việc</span>
                        </div>
                        <div class="legend-item">
                            <span class="material-icons status-icon no-shift-icon">remove_circle</span>
                            <span>Không có ca</span>
                        </div>
                        <div class="legend-item">
                            <span class="material-icons status-icon unknown-icon">help_outline</span>
                            <span>Chưa có dữ liệu</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
`,
pageScript = @` <script src="/assets/js/pharmacist/work_schedule.js" defer></script>`
)