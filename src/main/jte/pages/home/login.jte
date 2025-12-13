@param String error = null
@param String logout = null
@param org.springframework.security.web.csrf.CsrfToken csrf

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Pharmacist Platform Login</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
        /* Custom animation for the floating badge */
        @keyframes bounce-slow {
            0%, 100% { transform: translateY(-5%); }
            50% { transform: translateY(5%); }
        }
        .animate-bounce-slow {
            animation: bounce-slow 3s infinite ease-in-out;
        }
    </style>
</head>
<body class="min-h-screen w-full bg-gradient-to-br from-blue-50 [Write a concise vision statement that summarizes the purpose and intent of the new product and describes what the world will be like when it includes the product. The vision statement should reflect a balanced view that will satisfy the needs of diverse customers as well as those of the developing organization. It may be somewhat idealistic, but it should be grounded in the realities of existing or anticipated customer markets, enterprise architectures, organizational strategic directions, and cost and resource limitations]forto-indigo-100 flex items-center justify-center p-4 sm:p-6 lg:p-8">

<!-- Main Card Container -->
<div class="bg-white w-full max-w-5xl h-[600px] rounded-3xl shadow-2xl overflow-hidden flex relative">

    <!-- Left Side - Login Form -->
    <div class="w-full md:w-1/2 p-8 sm:p-12 lg:p-16 flex flex-col justify-center z-20 relative bg-white">
        <div class="max-w-sm mx-auto w-full">

            <h1 class="text-3xl font-bold text-slate-800 mb-2">Đăng nhập</h1>
            <p class="text-gray-500 mb-8 font-medium">Chào mừng đến với Pharmasys</p>

            @if(error != null)
                <div class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg mb-6" role="alert">
                    <p class="font-medium">Sai tài khoản hoặc mật khẩu</p>
                </div>
            @endif

            @if(logout != null)
                <div class="bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg mb-6" role="alert">
                    <p class="font-medium">Đăng xuất thành công</p>
                </div>
            @endif

            <form action="/login" method="POST" class="space-y-6">
                @if(csrf != null)
                    <input type="hidden" name="${csrf.getParameterName()}" value="${csrf.getToken()}">
                @endif

                <!-- Username Input -->
                <div class="relative w-full mb-4">
                    <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                        <!-- User Icon -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                    </div>

                    <input
                            type="text"
                            name="username"
                            id="username"
                            required
                            class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200"
                            placeholder="Tên đăng nhập"
                    />
                </div>

                <div class="space-y-1">
                    <!-- Password Input -->
                    <div class="relative w-full mb-4">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                            <!-- Lock Icon -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                        </div>

                        <input
                                id="passwordInput"
                                type="password"
                                name="password"
                                required
                                class="w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200"
                                placeholder="Mật khẩu"
                        />

                        <!-- Password Toggle -->
                        <button
                                type="button"
                                onclick="togglePassword()"
                                class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 transition-colors cursor-pointer focus:outline-none"
                        >
                            <!-- Eye Icon -->
                            <svg id="eyeIcon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
                            <!-- Eye Off Icon (Hidden by default) -->
                            <svg id="eyeOffIcon" class="hidden" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"/><path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"/><path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"/><line x1="2" x2="22" y1="2" y2="22"/></svg>
                        </button>
                    </div>

                    <div class="flex justify-end">
                        <a href="#" class="text-sm font-medium text-indigo-600 hover:text-indigo-800 transition-colors">
                            Quên mật khẩu?
                        </a>
                    </div>
                </div>

                <button
                        type="submit"
                        class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg shadow-md hover:shadow-lg transform transition-all duration-200 active:scale-[0.98]"
                >
                    Đăng nhập
                </button>
            </form>

        </div>
    </div>

    <!-- Right Side - Illustration & Background -->
    <div class="hidden md:block w-1/2 relative bg-sky-100">

        <!-- Custom SVG Wave Connector -->
        <div class="absolute top-0 bottom-0 left-0 w-16 md:w-24 lg:w-32 z-10 pointer-events-none overflow-hidden h-full">
            <svg
                    viewBox="0 0 100 100"
                    preserveAspectRatio="none"
                    class="h-full w-full text-white fill-current block"
                    style="transform: scaleX(1.5)"
            >
                <path d="M0 0 C 40 10 70 30 70 50 C 70 70 30 90 0 100 Z" />
            </svg>
        </div>

        <!-- Background Image / Illustration Area -->
        <div class="absolute inset-0 z-0">
            <img
                    src="/image/logo.png"
                    alt="Pharmacy Logo"
                    class="w-full h-full object-contain p-8"
            />
        </div>

        <!-- Floating Elements / Illustration Mimicry -->
        <div class="absolute inset-0 z-10 pointer-events-none">
            <!-- Badge at Top Right (Sky Area) -->
            <div class="absolute top-8 right-8 bg-white/90 backdrop-blur-sm p-4 rounded-2xl shadow-lg border border-white/50 animate-bounce-slow">
                <div class="flex items-center gap-3">
                    <div class="bg-green-100 p-2 rounded-full">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" class="text-green-600"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
                    </div>
                    <div>
                        <p class="text-xs text-gray-500 font-semibold uppercase tracking-wide">Trạng thái</p>
                        <p class="text-green-900 font-bold">Mở cửa !</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<!-- Background Decorative Shapes (Subtle) -->
<div class="fixed top-0 left-0 w-full h-full pointer-events-none -z-10 overflow-hidden">
    <div class="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-200/30 rounded-full blur-3xl"></div>
    <div class="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-indigo-200/30 rounded-full blur-3xl"></div>
</div>

<script>
    function togglePassword() {
        const passwordInput = document.getElementById('passwordInput');
        const eyeIcon = document.getElementById('eyeIcon');
        const eyeOffIcon = document.getElementById('eyeOffIcon');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            eyeIcon.classList.add('hidden');
            eyeOffIcon.classList.remove('hidden');
        } else {
            passwordInput.type = 'password';
            eyeIcon.classList.remove('hidden');
            eyeOffIcon.classList.add('hidden');
        }
    }
</script>
</body>
</html>
