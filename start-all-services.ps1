Clear-Host
Write-Host "🚀 Starting Loan Management System (All Microservices)..." -ForegroundColor Green

# ==================================================
# BASE PROJECT PATH  ✅ VERIFY THIS ONCE
# ==================================================
$BASE_PATH = "C:\Users\hp\Documents\Capstone\Loan--Management-System-Backend-"

# ==================================================
# COMMON FUNCTION TO START A SERVICE
# ==================================================
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
        "cd '$ServicePath'; Write-Host '$Name is running' -ForegroundColor Yellow; java -jar target\$JarName"

    Start-Sleep -Seconds 6
}

# ==================================================
# STARTUP ORDER (DO NOT CHANGE)
# ==================================================

# 1️⃣ CONFIG SERVER
Start-Service `
    -Name "Config Server" `
    -ServicePath "$BASE_PATH\config-server" `
    -JarName "config-server-1.0.0.jar"

# 2️⃣ EUREKA SERVER
Start-Service `
    -Name "Eureka Server" `
    -ServicePath "$BASE_PATH\eureka-server" `
    -JarName "eureka-server-1.0.0.jar"

# 3️⃣ AUTH SERVICE
Start-Service `
    -Name "Auth Service" `
    -ServicePath "$BASE_PATH\auth-service" `
    -JarName "auth-service-1.0.0.jar"

# 4️⃣ API GATEWAY
Start-Service `
    -Name "API Gateway" `
    -ServicePath "$BASE_PATH\api-gateway" `
    -JarName "api-gateway-1.0.0.jar"

# 5️⃣ CUSTOMER SERVICE
Start-Service `
    -Name "Customer Service" `
    -ServicePath "$BASE_PATH\customer-service" `
    -JarName "customer-service-1.0.jar"

# 6️⃣ LOAN APPLICATION SERVICE
Start-Service `
    -Name "Loan Application Service" `
    -ServicePath "$BASE_PATH\loan-application-service" `
    -JarName "loan-application-service-0.0.1-SNAPSHOT.jar"

# 7️⃣ LOAN PROCESSING SERVICE
Start-Service `
    -Name "Loan Processing Service" `
    -ServicePath "$BASE_PATH\loan-processing-service" `
    -JarName "loan-processing-service-0.0.1-SNAPSHOT.jar"

# 8️⃣ PAYMENT SERVICE
Start-Service `
    -Name "Payment Service" `
    -ServicePath "$BASE_PATH\payment-service" `
    -JarName "payment-service-0.0.1-SNAPSHOT.jar"

# 9️⃣ NOTIFICATION SERVICE
Start-Service `
    -Name "Notification Service" `
    -ServicePath "$BASE_PATH\notification-service" `
    -JarName "notification-service-0.0.1-SNAPSHOT.jar"

# 🔟 ANALYTICS SERVICE
Start-Service `
    -Name "Analytics Service" `
    -ServicePath "$BASE_PATH\analytics-service" `
    -JarName "analytics-service-1.0.jar"

Write-Host "`n✅ ALL MICROSERVICES STARTED SUCCESSFULLY" -ForegroundColor Green
Write-Host "🔎 Check Eureka at: http://localhost:8761" -ForegroundColor Yellow
