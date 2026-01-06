@echo off
setlocal enabledelayedexpansion

REM =========================================
REM Load .env file (optional)
REM =========================================
if exist .env (
    for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
        set "key=%%a"
        if "!key:~0,1!" neq "#" (
            set "%%a=%%b"
        )
    )
) else (
    echo WARNING: .env file not found.
)

echo.
echo =========================================
echo 🚀 Starting Loan Management System
echo =========================================
echo.

REM =========================================
REM INFRA (Kafka assumed already running)
REM =========================================

echo Starting Config Server (8080)...
start "Config Server" cmd /k ^
"java -jar config-server\target\config-server-1.0.0.jar || (echo CONFIG SERVER CRASHED & pause)"
timeout /t 15 /nobreak > nul

echo Starting Eureka Server (8761)...
start "Eureka Server" cmd /k ^
"java -jar eureka-server\target\eureka-server-1.0.0.jar || (echo EUREKA SERVER CRASHED & pause)"
timeout /t 20 /nobreak > nul

REM =========================================
REM CORE SERVICES
REM =========================================

echo Starting Auth Service...
start "Auth Service" cmd /k ^
"java -jar auth-service\target\auth-service-1.0.0.jar || (echo AUTH SERVICE CRASHED & pause)"
timeout /t 10 /nobreak > nul

echo Starting API Gateway (8080)...
start "API Gateway" cmd /k ^
"java -jar api-gateway\target\api-gateway-1.0.0.jar || (echo GATEWAY CRASHED & pause)"
timeout /t 10 /nobreak > nul

echo Starting Customer Service...
start "Customer Service" cmd /k ^
"java -jar customer-service\target\customer-service-1.0.jar || (echo CUSTOMER SERVICE CRASHED & pause)"
timeout /t 5 /nobreak > nul

echo Starting Loan Application Service...
start "Loan Application Service" cmd /k ^
"java -jar loan-application-service\target\loan-application-service-0.0.1-SNAPSHOT.jar || (echo LOAN APP SERVICE CRASHED & pause)"
timeout /t 5 /nobreak > nul

echo Starting Loan Processing Service...
start "Loan Processing Service" cmd /k ^
"java -jar loan-processing-service\target\loan-processing-service-0.0.1-SNAPSHOT.jar || (echo LOAN PROCESSING CRASHED & pause)"
timeout /t 5 /nobreak > nul

echo Starting Payment Service...
start "Payment Service" cmd /k ^
"java -jar payment-service\target\payment-service-0.0.1-SNAPSHOT.jar || (echo PAYMENT SERVICE CRASHED & pause)"
timeout /t 5 /nobreak > nul

echo Starting Notification Service...
start "Notification Service" cmd /k ^
"java -jar notification-service\target\notification-service-0.0.1-SNAPSHOT.jar || (echo NOTIFICATION SERVICE CRASHED & pause)"
timeout /t 5 /nobreak > nul

echo Starting Analytics Service...
start "Analytics Service" cmd /k ^
"java -jar analytics-service\target\analytics-service-1.0.jar || (echo ANALYTICS SERVICE CRASHED & pause)"

echo.
echo =========================================
echo ✅ ALL SERVICES STARTED
echo Eureka:  http://localhost:8761
echo Gateway: http://localhost:8080
echo =========================================
pause
