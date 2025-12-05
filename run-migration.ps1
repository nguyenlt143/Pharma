# Script để chạy Unit Conversion Migration
# Chạy file này bằng cách: .\run-migration.ps1

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Unit Conversion Migration Script" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra xem ứng dụng có đang chạy không
Write-Host "Đang kiểm tra kết nối đến server..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080" -Method GET -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓ Server đang chạy!" -ForegroundColor Green
} catch {
    Write-Host "✗ Không thể kết nối đến server!" -ForegroundColor Red
    Write-Host "Vui lòng đảm bảo ứng dụng đang chạy trên http://localhost:8080" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Nhấn Enter để thoát"
    exit
}

Write-Host ""
Write-Host "Đang gọi migration endpoint..." -ForegroundColor Yellow

try {
    # Gọi migration endpoint
    $migrationResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/accounts/unit-conversion-migration" -Method POST -ContentType "application/json"

    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host "KẾT QUẢ MIGRATION" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Green

    if ($migrationResponse.success) {
        Write-Host "✓ THÀNH CÔNG!" -ForegroundColor Green
        Write-Host ""
        Write-Host $migrationResponse.message -ForegroundColor Green
    } else {
        Write-Host "✗ LỖI!" -ForegroundColor Red
        Write-Host ""
        Write-Host $migrationResponse.message -ForegroundColor Red
    }

} catch {
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Red
    Write-Host "LỖI KHI GỌI API" -ForegroundColor Red
    Write-Host "=====================================" -ForegroundColor Red
    Write-Host ""

    # Kiểm tra xem có phải lỗi authentication không
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✗ Lỗi Authentication (401)" -ForegroundColor Red
        Write-Host ""
        Write-Host "Endpoint này yêu cầu đăng nhập với quyền ADMIN." -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Giải pháp:" -ForegroundColor Cyan
        Write-Host "1. Mở trình duyệt và đăng nhập vào hệ thống với tài khoản ADMIN" -ForegroundColor White
        Write-Host "2. Mở Developer Tools (F12)" -ForegroundColor White
        Write-Host "3. Vào tab Console" -ForegroundColor White
        Write-Host "4. Chạy lệnh sau:" -ForegroundColor White
        Write-Host ""
        Write-Host "   fetch('/api/admin/accounts/unit-conversion-migration', {method: 'POST'})" -ForegroundColor Green
        Write-Host "   .then(r => r.json())" -ForegroundColor Green
        Write-Host "   .then(data => console.log(data))" -ForegroundColor Green
        Write-Host ""
    } elseif ($_.Exception.Response.StatusCode -eq 403) {
        Write-Host "✗ Lỗi Forbidden (403)" -ForegroundColor Red
        Write-Host "Tài khoản không có quyền truy cập endpoint này." -ForegroundColor Yellow
    } else {
        Write-Host "Chi tiết lỗi:" -ForegroundColor Yellow
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Xem console của ứng dụng để biết chi tiết về quá trình migration!" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Nhấn Enter để thoát"

