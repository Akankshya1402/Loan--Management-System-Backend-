Clear-Host
Write-Host "🚀 Starting Loan Management System..." -ForegroundColor Green

# ==================================================
# BASE PATH (RELATIVE → WORKS FOR GITHUB CLONES)
# ==================================================
$BASE_PATH = Get-Location

function Start-Service {
    param (
        [string]$Name,
        [string]$ServicePath,
        [string]$JarName
    )

    Write-Host "▶ Starting $Name..." -ForegroundColor Cyan

    Start-Process powershell -ArgumentList `
        "-NoExit",
        "-Command",
        "cd '$ServicePath'; Write-Host '$Name running' -ForegroundColor Yellow; java -jar target\$JarName"

    Start-Sleep -Seconds 6
}

# ===============================
# START ORDER (IMPORTANT)
# ===============================

Start-Service "Config Server" "$BASE_PATH\config-server" "config-server-1.0.0.jar"
Start-Service "Eureka Server" "$BASE_PATH\eureka-server" "eureka-server-1.0.0.jar"
Start-Service "Auth Service" "$BASE_PATH\auth-service" "auth-service-1.0.0.jar"
Start-Service "API Gateway" "$BASE_PATH\api-gateway" "api-gateway-1.0.0.jar"
Start-Service "Customer Service" "$BASE_PATH\customer-service" "customer-service-1.0.jar"
Start-Service "Loan Application Service" "$BASE_PATH\loan-application-service" "loan-application-service-0.0.1-SNAPSHOT.jar"
Start-Service "Loan Processing Service" "$BASE_PATH\loan-processing-service" "loan-processing-service-0.0.1-SNAPSHOT.jar"
Start-Service "Payment Service" "$BASE_PATH\payment-service" "payment-service-0.0.1-SNAPSHOT.jar"
Start-Service "Notification Service" "$BASE_PATH\notification-service" "notification-service-0.0.1-SNAPSHOT.jar"
Start-Service "Analytics Service" "$BASE_PATH\analytics-service" "analytics-service-1.0.jar"

Write-Host "`n✅ ALL SERVICES STARTED" -ForegroundColor Green
Write-Host "🔎 Eureka Dashboard: http://localhost:8761" -ForegroundColor Yellow
