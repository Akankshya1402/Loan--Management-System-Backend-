Clear-Host
Write-Host "🛑 Stopping Loan Management System Microservices..." -ForegroundColor Red

# ======================================
# LIST OF SERVICE JAR NAMES (MATCH EXACT)
# ======================================
$services = @(
    "config-server-1.0.0.jar",
    "eureka-server-1.0.0.jar",
    "auth-service-1.0.0.jar",
    "api-gateway-1.0.0.jar",
    "customer-service-1.0.jar",
    "loan-application-service-0.0.1-SNAPSHOT.jar",
    "loan-processing-service-0.0.1-SNAPSHOT.jar",
    "payment-service-0.0.1-SNAPSHOT.jar",
    "notification-service-0.0.1-SNAPSHOT.jar",
    "analytics-service-1.0.jar"
)

foreach ($jar in $services) {
    Write-Host "Stopping $jar ..." -ForegroundColor Yellow

    Get-Process java -ErrorAction SilentlyContinue |
        Where-Object { $_.Path -like "*$jar*" } |
        Stop-Process -Force
}

Write-Host "`n✅ ALL MICROSERVICES STOPPED SUCCESSFULLY" -ForegroundColor Green
