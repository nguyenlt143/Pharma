<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Pharmacist Platform Login</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

    <!-- Tailwind -->
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>

    <!-- Tailwind Config -->
    <script>
        tailwind.config = {
          darkMode: "class",
          theme: {
            extend: {
              colors: {
                primary: "#6366F1",
                "background-light": "#E0E7FF",
                "background-dark": "#111827",
              },
              fontFamily: {
                display: ["Poppins", "sans-serif"],
              },
              borderRadius: {
                DEFAULT: "1rem",
              },
            },
          },
        };
    </script>

    <!-- Custom CSS -->
    <link rel="stylesheet" href="style.css">
</head>
<body class="bg-background-light dark:bg-background-dark font-display flex items-center justify-center min-h-screen">

<div class="bg-white dark:bg-slate-800 shadow-2xl rounded-2xl w-full max-w-4xl flex overflow-hidden">

    <!-- Form Section -->
    <div class="w-full lg:w-1/2 p-8 sm:p-12 flex flex-col justify-center">
        <h1 class="text-3xl font-bold text-slate-800 dark:text-white mb-4">Login</h1>
        <p class="text-slate-600 dark:text-slate-400 mb-8">Welcome to Pharmacist Platform</p>

        <form class="space-y-6" id="loginForm">
            <!-- Username -->
            <div>
                <label class="sr-only" for="username">Username or email</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">person</span>
                    <input id="username" name="username" type="text" placeholder="Username or email"
                           class="w-full pl-10 pr-4 py-3 bg-slate-50 dark:bg-slate-700 text-slate-900 dark:text-slate-300 border border-slate-200 dark:border-slate-600 rounded-lg focus:ring-primary focus:border-primary placeholder-slate-400 dark:placeholder-slate-500"/>
                </div>
            </div>

            <!-- Password -->
            <div>
                <label class="sr-only" for="password">Password</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">lock</span>
                    <input id="password" name="password" type="password" placeholder="Password"
                           class="w-full pl-10 pr-10 py-3 bg-slate-50 dark:bg-slate-700 text-slate-900 dark:text-slate-300 border border-slate-200 dark:border-slate-600 rounded-lg focus:ring-primary focus:border-primary placeholder-slate-400 dark:placeholder-slate-500"/>
                    <button type="button" id="togglePassword"
                            class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200">
                        <span class="material-symbols-outlined">visibility</span>
                    </button>
                </div>
            </div>

            <div class="text-right">
                <a href="#" class="text-sm font-medium text-primary hover:underline">Forgot?</a>
            </div>

            <div>
                <button type="submit"
                        class="w-full bg-primary text-white font-semibold py-3 px-4 rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary dark:focus:ring-offset-slate-800 transition-colors duration-300">
                    Login
                </button>
            </div>
        </form>
    </div>

    <!-- Illustration Section -->
    <div class="hidden lg:block lg:w-1/2 relative">
        <div class="absolute inset-0 bg-indigo-100 dark:bg-slate-900 clip-path-wave"></div>
        <div class="absolute inset-0 flex items-center justify-center p-8">
            <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuBmZSd_GxFIiNDExMK2R-yCnSbs8qkY7tix9k0qRlYFgKNjLz-B4WKmOAXVHxWq-WfNjWHQmxcbgqGwums_4dlsB_Lrjsz1zpVOD2QKVHZW_cpppSIkNvzb1x_Eb1LRaZZLuQVRTpm05WnXljL5kBdSXhJISpiFqNFEmh0Ifdid_YoOdSKix3ny8qe2hcNnEZUc03F4ceWYDa6z5PMHcwmLM_2FIB92lBgoXeXn3x5RfDkjQjXLxtu-pnqHAuOxPVsLhIej_qY1iiFS"
                 alt="Pharmacist illustration"
                 class="max-w-full h-auto object-contain">
        </div>
    </div>
</div>

<script src="script.js"></script>
</body>
</html>
