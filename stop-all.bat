@echo off
echo 🛑 Stopping Loan Management System...

for %%j in (
    config-server-1.0.0.jar
    eureka-server-1.0.0.jar
    auth-service-1.0.0.jar
    api-gateway-1.0.0.jar
    customer-service-1.0.jar
    loan-application-service-0.0.1-SNAPSHOT.jar
    loan-processing-service-0.0.1-SNAPSHOT.jar
    payment-service-0.0.1-SNAPSHOT.jar
    notification-service-0.0.1-SNAPSHOT.jar
    analytics-service-1.0.jar
) do (
    taskkill /F /IM java.exe /FI "WINDOWTITLE eq %%j*" > nul 2>&1
)

echo ✅ ALL SERVICES STOPPED
pause
