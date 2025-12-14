@import gg.jte.Content

@param Content content
@param Content headContent = @``
@param Content pageScript = @``
@param String userName = "Dược sĩ"
@param String userRole = "Dược sĩ"

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhà Thuốc</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
    <link rel="stylesheet" href="/assets/css/pharmacist/layout.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/2.3.4/css/dataTables.bootstrap5.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <script src="https://cdn.jsdelivr.net/npm/dayjs@1/dayjs.min.js"></script>
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/2.3.4/js/dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/2.3.4/js/dataTables.bootstrap5.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
    <style>
        /* Override to ensure sidebar matches admin.jte exactly and header/footer styles work */
        .sidebar {
            width: 286px !important;
            background-color: #0F172A !important;
            color: #FFFFFF !important;
            display: flex !important;
            flex-direction: column !important;
            position: fixed !important;
            height: 100vh !important;
            left: 0 !important;
            top: 0 !important;
            z-index: 1000 !important;
        }

        .sidebar-header {
            padding: 16px !important;
            border-bottom: 1px solid #374151 !important;
        }

        .sidebar-title {
            font-weight: 700 !important;
            font-size: 20px !important;
            line-height: 28px !important;
            color: #FFFFFF !important;
            margin: 0 !important;
        }

        .sidebar-subtitle {
            font-size: 12px !important;
            color: #9CA3AF !important;
            margin: 4px 0 0 0 !important;
        }

        .sidebar-nav {
            flex: 1 !important;
            overflow-y: auto !important;
        }

        .nav-list {
            list-style: none !important;
            padding: 0 !important;
            margin: 0 !important;
        }

        .nav-item {
            margin: 0 !important;
        }

        .nav-link {
            display: flex !important;
            align-items: center !important;
            padding: 12px 16px !important;
            color: #D1D5DB !important;
            text-decoration: none !important;
            transition: all 0.2s !important;
        }

        .nav-link:hover {
            background-color: #1F2937 !important;
            color: #FFFFFF !important;
        }

        .nav-link.active {
            background-color: #2563EB !important;
            color: #FFFFFF !important;
            border-radius: 6px !important;
            margin: 0 8px !important;
        }

        .nav-icon {
            font-size: 20px !important;
            margin-right: 12px !important;
        }

        .nav-text {
            font-size: 14px !important;
            font-weight: 500 !important;
        }

        .sidebar-footer {
            border-top: 1px solid #374151 !important;
            padding: 16px !important;
        }

        .logout-link {
            display: flex !important;
            align-items: center !important;
            padding: 12px !important;
            color: #D1D5DB !important;
            text-decoration: none !important;
            border-radius: 6px !important;
            transition: all 0.2s !important;
        }

        .logout-link:hover {
            background-color: #1F2937 !important;
            color: #FFFFFF !important;
        }

        .logout-link .material-icons {
            margin-right: 12px !important;
        }

        /* Main Content Layout */
        .main-content {
            flex: 1 !important;
            margin-left: 286px !important;
            display: flex !important;
            flex-direction: column !important;
            min-height: 100vh !important;
            background-color: #F9FAFB !important;
            padding: 0 !important;
            width: calc(100% - 286px) !important;
        }

        .content-area {
            flex: 1 !important;
            padding: 24px !important;
            overflow-y: auto !important;
            overflow-x: auto !important;
        }

        /* Header Styles */
        .main-header {
            height: 60px !important;
            background-color: #FFFFFF !important;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
            display: flex !important;
            align-items: center !important;
            justify-content: space-between !important;
            padding: 0 24px !important;
            gap: 24px !important;
            flex-shrink: 0 !important;
            border-bottom: 1px solid #E5E7EB !important;
            z-index: 100 !important;
            position: sticky !important;
            top: 0 !important;
            width: 100% !important;
        }

        .header-left {
            display: flex !important;
            align-items: center !important;
            gap: 12px !important;
        }

        .header-left .material-icons {
            color: #10B981 !important;
            font-size: 24px !important;
        }

        .greeting-text {
            font-size: 16px !important;
            color: #6B7280 !important;
            font-weight: 400 !important;
        }

        .greeting-text strong {
            color: #111827 !important;
            font-weight: 600 !important;
        }

        .header-divider {
            color: #D1D5DB !important;
            font-size: 14px !important;
            user-select: none !important;
        }

        .header-date {
            display: flex !important;
            align-items: center !important;
            gap: 6px !important;
            padding: 6px 12px !important;
            background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%) !important;
            border-radius: 20px !important;
            border: 1px solid #BAE6FD !important;
            font-size: 14px !important;
            font-weight: 500 !important;
            color: #0369A1 !important;
            transition: all 0.2s ease !important;
        }

        .header-date:hover {
            background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%) !important;
            border-color: #7DD3FC !important;
            transform: translateY(-1px) !important;
        }

        .header-date .material-icons {
            font-size: 18px !important;
            color: #0EA5E9 !important;
        }

        .header-weather {
            display: flex !important;
            align-items: center !important;
            gap: 6px !important;
            padding: 6px 12px !important;
            background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%) !important;
            border-radius: 20px !important;
            border: 1px solid #FCD34D !important;
            font-size: 14px !important;
            font-weight: 600 !important;
            color: #B45309 !important;
            transition: all 0.2s ease !important;
        }

        .header-weather:hover {
            background: linear-gradient(135deg, #FDE68A 0%, #FCD34D 100%) !important;
            border-color: #FBBF24 !important;
            transform: translateY(-1px) !important;
        }

        .header-weather .material-icons {
            font-size: 20px !important;
            color: #F59E0B !important;
        }

        #temperature {
            font-family: 'Roboto Mono', 'Courier New', monospace !important;
            letter-spacing: 0.02em !important;
        }

        .header-right {
            display: flex !important;
            align-items: center !important;
            gap: 12px !important;
        }

        .profile-link {
            display: flex !important;
            align-items: center !important;
            gap: 8px !important;
            padding: 10px 16px !important;
            background-color: #F9FAFB !important;
            border-radius: 8px !important;
            text-decoration: none !important;
            color: #6B7280 !important;
            font-size: 15px !important;
            font-weight: 500 !important;
            transition: all 0.2s ease !important;
            border: 1px solid #E5E7EB !important;
        }

        .profile-link:hover {
            background-color: #EEF2FF !important;
            border-color: #C7D2FE !important;
            color: #4338CA !important;
            transform: translateY(-1px) !important;
            box-shadow: 0 4px 12px rgba(67, 56, 202, 0.15) !important;
        }

        .profile-link .material-icons {
            font-size: 22px !important;
            color: #6B7280 !important;
            transition: all 0.2s ease !important;
        }

        .profile-link:hover .material-icons {
            color: #4338CA !important;
        }

        .profile-text {
            font-weight: 500 !important;
            white-space: nowrap !important;
        }

        /* Footer Styles */
        .main-footer {
            height: 42px !important;
            background: linear-gradient(180deg, #FAFBFC 0%, #FFFFFF 100%) !important;
            border-top: 2px solid #E2E8F0 !important;
            display: flex !important;
            align-items: center !important;
            justify-content: space-between !important;
            padding: 0 28px !important;
            flex-shrink: 0 !important;
            font-size: 13px !important;
            color: #64748B !important;
            box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.06) !important;
            position: sticky !important;
            bottom: 0 !important;
            width: 100% !important;
            z-index: 100 !important;
        }

        .main-footer::before {
            content: '' !important;
            position: absolute !important;
            top: 0 !important;
            left: 0 !important;
            right: 0 !important;
            height: 1px !important;
            background: linear-gradient(90deg, transparent 0%, #818CF8 20%, #6366F1 50%, #818CF8 80%, transparent 100%) !important;
            opacity: 0.5 !important;
        }

        .footer-left,
        .footer-right {
            display: flex !important;
            align-items: center !important;
            gap: 14px !important;
        }

        .footer-brand {
            font-weight: 800 !important;
            background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 50%, #6366F1 100%) !important;
            -webkit-background-clip: text !important;
            -webkit-text-fill-color: transparent !important;
            background-clip: text !important;
            font-size: 15px !important;
            letter-spacing: -0.02em !important;
            transition: all 0.3s ease !important;
        }

        .footer-brand:hover {
            transform: scale(1.05) !important;
            filter: brightness(1.2) !important;
        }

        .footer-version {
            font-weight: 600 !important;
            color: #94A3B8 !important;
            background-color: #F1F5F9 !important;
            padding: 2px 10px !important;
            border-radius: 12px !important;
            font-size: 12px !important;
            border: 1px solid #E2E8F0 !important;
            transition: all 0.2s ease !important;
        }

        .footer-version:hover {
            background-color: #E0E7FF !important;
            border-color: #C7D2FE !important;
            color: #6366F1 !important;
            transform: translateY(-1px) !important;
        }

        .footer-divider {
            color: #CBD5E1 !important;
            font-weight: 300 !important;
            user-select: none !important;
        }

        .footer-copyright {
            display: flex !important;
            align-items: center !important;
            gap: 4px !important;
            color: #94A3B8 !important;
            font-weight: 500 !important;
            padding: 4px 10px !important;
            border-radius: 8px !important;
            transition: all 0.2s ease !important;
        }

        .footer-copyright:hover {
            background-color: #F8FAFC !important;
            color: #64748B !important;
        }

        .footer-copyright .material-icons {
            font-size: 15px !important;
            opacity: 0.8 !important;
        }

        .footer-status {
            display: flex !important;
            align-items: center !important;
            gap: 7px !important;
            font-weight: 600 !important;
            padding: 4px 12px !important;
            border-radius: 20px !important;
            background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%) !important;
            border: 1px solid #86EFAC !important;
            transition: all 0.3s ease !important;
        }

        .footer-status.online {
            color: #047857 !important;
        }

        .footer-status:hover {
            transform: translateY(-1px) !important;
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2) !important;
        }

        .status-dot {
            width: 9px !important;
            height: 9px !important;
            border-radius: 50% !important;
            background: radial-gradient(circle, #10B981 0%, #059669 100%) !important;
            box-shadow: 0 0 8px rgba(16, 185, 129, 0.6), 0 0 16px rgba(16, 185, 129, 0.4) !important;
            animation: pulse-dot 2.5s ease-in-out infinite !important;
            position: relative !important;
        }

        .status-dot::before {
            content: '' !important;
            position: absolute !important;
            inset: -3px !important;
            border-radius: 50% !important;
            border: 2px solid #10B981 !important;
            opacity: 0 !important;
            animation: ripple-dot 2.5s ease-in-out infinite !important;
        }

        @keyframes pulse-dot {
            0%, 100% {
                opacity: 1;
                transform: scale(1);
            }
            50% {
                opacity: 0.8;
                transform: scale(1.15);
            }
        }

        @keyframes ripple-dot {
            0% {
                opacity: 1;
                transform: scale(0.8);
            }
            50% {
                opacity: 0.5;
                transform: scale(1.2);
            }
            100% {
                opacity: 0;
                transform: scale(1.5);
            }
        }

        .footer-time {
            display: flex !important;
            align-items: center !important;
            gap: 6px !important;
            font-weight: 600 !important;
            color: #475569 !important;
            padding: 4px 12px !important;
            background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%) !important;
            border-radius: 20px !important;
            border: 1px solid #E2E8F0 !important;
            transition: all 0.2s ease !important;
        }

        .footer-time:hover {
            background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%) !important;
            border-color: #C7D2FE !important;
            transform: translateY(-1px) !important;
        }

        .footer-time .material-icons {
            font-size: 17px !important;
            color: #6366F1 !important;
        }
    </style>
    ${headContent}
</head>

<body x-data="{ 'loaded': true }">

<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">PharmaSys</h1>
            <p class="sidebar-subtitle">Dược sĩ</p>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/pharmacist/pos" class="nav-link">
                        <span class="material-icons nav-icon">shopping_cart</span>
                        <span class="nav-text">Bán hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/invoices" class="nav-link">
                        <span class="material-icons nav-icon">receipt_long</span>
                        <span class="nav-text">Danh sách đơn thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/shifts" class="nav-link">
                        <span class="material-icons nav-icon">bar_chart</span>
                        <span class="nav-text">Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/work" class="nav-link">
                        <span class="material-icons nav-icon">schedule</span>
                        <span class="nav-text">Lịch làm việc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/revenues" class="nav-link">
                        <span class="material-icons nav-icon">assessment</span>
                        <span class="nav-text">Báo cáo doanh thu</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <a href="/logout" class="logout-link">
                <span class="material-icons">logout</span>
                <span>Đăng xuất</span>
            </a>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Header -->
        <header class="main-header">
            <div class="header-left">
                <span class="material-icons">local_pharmacy</span>
                <span class="greeting-text">Xin Chào, <strong>${userName}</strong></span>
                <span class="header-divider">|</span>
                <span class="header-date">
                    <span class="material-icons">calendar_today</span>
                    <span id="currentDate">Loading...</span>
                </span>
                <span class="header-divider">|</span>
                <span class="header-weather">
                    <span class="material-icons" id="weatherIcon">wb_sunny</span>
                    <span id="temperature">--°C</span>
                </span>
            </div>
            <div class="header-right">
                <a href="/pharmacist/profile" class="profile-link">
                    <span class="material-icons">person</span>
                    <span class="profile-text">Thông tin cá nhân</span>
                </a>
            </div>
        </header>

        <!-- Content Area -->
        <div class="content-area">
            ${content}
        </div>

        <!-- Footer -->
        <footer class="main-footer">
            <div class="footer-left">
                <span class="footer-brand">PharmaSys</span>
                <span class="footer-divider">|</span>
                <span class="footer-version">v2.1</span>
                <span class="footer-divider">|</span>
                <span class="footer-copyright">
                    <span class="material-icons">copyright</span>
                    <span>2025</span>
                </span>
            </div>
            <div class="footer-right">
                <span class="footer-status online">
                    <span class="status-dot"></span>
                    <span>Đã kết nối</span>
                </span>
                <span class="footer-divider">|</span>
                <span class="footer-time">
                    <span class="material-icons">schedule</span>
                    <span id="serverTime">00:00 AM</span>
                </span>
            </div>
        </footer>
    </main>
</div>

${pageScript}

<script>
    // Active nav link highlighting
    document.addEventListener('DOMContentLoaded', function() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link');

        let bestMatch = null;
        navLinks.forEach(link => {
            link.classList.remove('active');
            const href = link.getAttribute('href');
            if (href && currentPath.startsWith(href)) {
                if (!bestMatch || href.length > bestMatch.getAttribute('href').length) {
                    bestMatch = link;
                }
            }
        });

        if (bestMatch) {
            bestMatch.classList.add('active');
        }

        // Initialize server time
        updateServerTime();
        setInterval(updateServerTime, 1000);

        // Initialize current date
        updateCurrentDate();
        // Update date at midnight
        setInterval(updateCurrentDate, 60000); // Check every minute

        // Initialize weather
        updateWeather();
        // Update weather every 30 minutes
        setInterval(updateWeather, 1800000);
    });

    // Update server time
    function updateServerTime() {
        const now = new Date();
        const hours = now.getHours();
        const minutes = now.getMinutes().toString().padStart(2, '0');
        const ampm = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;

        const timeString = displayHours + ':' + minutes + ' ' + ampm;
        const serverTimeEl = document.getElementById('serverTime');
        if (serverTimeEl) {
            serverTimeEl.textContent = timeString;
        }
    }

    // Update current date
    function updateCurrentDate() {
        const now = new Date();
        const daysOfWeek = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
        const dayName = daysOfWeek[now.getDay()];
        const day = now.getDate().toString().padStart(2, '0');
        const month = (now.getMonth() + 1).toString().padStart(2, '0');
        const year = now.getFullYear();

        const dateString = dayName + ', ' + day + '/' + month + '/' + year;
        const dateElement = document.getElementById('currentDate');
        if (dateElement) {
            dateElement.textContent = dateString;
        }
    }

    // Update weather (using OpenWeatherMap API or fallback to simulated data)
    function updateWeather() {
        // Try to get user's location
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    const lat = position.coords.latitude;
                    const lon = position.coords.longitude;
                    fetchWeatherData(lat, lon);
                },
                function(error) {
                    // If geolocation fails, use default location (Hanoi, Vietnam)
                    console.log('Geolocation error, using default location');
                    fetchWeatherData(21.0285, 105.8542); // Hanoi coordinates
                }
            );
        } else {
            // Geolocation not supported, use default location
            fetchWeatherData(21.0285, 105.8542); // Hanoi coordinates
        }
    }

    // Fetch weather data from OpenWeatherMap API
    function fetchWeatherData(lat, lon) {
        // OpenWeatherMap API key (you should use your own key)
        // For demo, using a fallback simulation if API fails
        const apiKey = 'YOUR_API_KEY_HERE'; // Replace with actual key
        const apiUrl = 'https://api.openweathermap.org/data/2.5/weather?lat=' + lat + '&lon=' + lon + '&units=metric&appid=' + apiKey;

        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                if (data.main && data.main.temp && data.weather && data.weather[0]) {
                    const temp = Math.round(data.main.temp);
                    const weatherCondition = data.weather[0].main;
                    updateWeatherDisplay(temp, weatherCondition);
                } else {
                    // API failed, use simulated data
                    useSimulatedWeather();
                }
            })
            .catch(error => {
                console.log('Weather API error, using simulated data:', error);
                useSimulatedWeather();
            });
    }

    // Use simulated weather data
    function useSimulatedWeather() {
        const now = new Date();
        const hour = now.getHours();

        // Simulate temperature based on time of day
        let temp;
        let condition;

        if (hour >= 6 && hour < 12) {
            // Morning: 20-25°C
            temp = Math.floor(Math.random() * 6) + 20;
            condition = 'Clear';
        } else if (hour >= 12 && hour < 18) {
            // Afternoon: 28-33°C
            temp = Math.floor(Math.random() * 6) + 28;
            condition = 'Clear';
        } else if (hour >= 18 && hour < 22) {
            // Evening: 22-27°C
            temp = Math.floor(Math.random() * 6) + 22;
            condition = 'Clear';
        } else {
            // Night: 18-22°C
            temp = Math.floor(Math.random() * 5) + 18;
            condition = 'Clear';
        }

        updateWeatherDisplay(temp, condition);
    }

    // Update weather display
    function updateWeatherDisplay(temp, condition) {
        const tempElement = document.getElementById('temperature');
        const iconElement = document.getElementById('weatherIcon');

        if (tempElement) {
            tempElement.textContent = temp + '°C';
        }

        if (iconElement) {
            // Set icon based on weather condition
            let icon;
            switch (condition.toLowerCase()) {
                case 'clear':
                    icon = 'wb_sunny';
                    break;
                case 'clouds':
                    icon = 'cloud';
                    break;
                case 'rain':
                case 'drizzle':
                    icon = 'rainy';
                    break;
                case 'thunderstorm':
                    icon = 'thunderstorm';
                    break;
                case 'snow':
                    icon = 'ac_unit';
                    break;
                case 'mist':
                case 'fog':
                    icon = 'foggy';
                    break;
                default:
                    icon = 'wb_sunny';
            }
            iconElement.textContent = icon;
        }
    }
</script>
<script src="/assets/js/pharmacist/layout.js" defer></script>
</body>
</html>
