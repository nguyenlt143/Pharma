#!/usr/bin/env pwsh

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  Shifts Data Loading Debug Script" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Check if application is already running
$process = Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*pharma*" }
if ($process) {
    Write-Host "⚠️  Application already running (PID: $($process.Id))" -ForegroundColor Yellow
    $answer = Read-Host "Do you want to restart? (y/n)"
    if ($answer -eq "y") {
        Write-Host "Stopping existing process..." -ForegroundColor Yellow
        Stop-Process -Id $process.Id -Force
        Start-Sleep -Seconds 3
    } else {
        Write-Host "Keeping existing process..." -ForegroundColor Green
    }
}

# Start application
Write-Host ""
Write-Host "📦 Starting Spring Boot application..." -ForegroundColor Green
Write-Host ""

$gradlewPath = Join-Path $PSScriptRoot "gradlew.bat"

# Start the application in a new window
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; .\gradlew.bat bootRun"

Write-Host "⏳ Waiting for application to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check if application is up
$maxRetries = 30
$retry = 0
$isUp = $false

while ($retry -lt $maxRetries -and -not $isUp) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $isUp = $true
        }
    } catch {
        # Ignore errors, just retry
    }

    if (-not $isUp) {
        Write-Host "." -NoNewline -ForegroundColor Yellow
        Start-Sleep -Seconds 2
        $retry++
    }
}

Write-Host ""

if ($isUp) {
    Write-Host "✅ Application is running!" -ForegroundColor Green
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "  Test URLs" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Main shifts page:" -ForegroundColor White
    Write-Host "   http://localhost:8080/pharmacist/shifts" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "2. Debug test page:" -ForegroundColor White
    Write-Host "   file:///$PSScriptRoot/shifts-debug-test.html" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "3. Direct API endpoint:" -ForegroundColor White
    Write-Host "   http://localhost:8080/pharmacist/all/shift" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""

    $answer = Read-Host "Do you want to open the main page in browser? (y/n)"
    if ($answer -eq "y") {
        Start-Process "http://localhost:8080/pharmacist/shifts"
        Write-Host "✅ Browser opened!" -ForegroundColor Green
    }

    Write-Host ""
    $answer = Read-Host "Do you want to open the debug test page? (y/n)"
    if ($answer -eq "y") {
        $debugPath = Join-Path $PSScriptRoot "shifts-debug-test.html"
        Start-Process $debugPath
        Write-Host "✅ Debug page opened!" -ForegroundColor Green
    }

    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "  Debug Instructions" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Open Developer Console (F12) in your browser" -ForegroundColor White
    Write-Host "2. Go to Console tab" -ForegroundColor White
    Write-Host "3. Check for API call logs and errors" -ForegroundColor White
    Write-Host "4. For standalone debug, use shifts-debug-test.html" -ForegroundColor White
    Write-Host ""
    Write-Host "See SHIFTS_DATA_LOADING_FIX.md for detailed troubleshooting" -ForegroundColor Cyan
    Write-Host ""

} else {
    Write-Host "❌ Application failed to start within expected time" -ForegroundColor Red
    Write-Host "   Check the application window for error messages" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

