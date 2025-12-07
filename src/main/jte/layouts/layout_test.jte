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
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&family=Inter:wght@400&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
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
