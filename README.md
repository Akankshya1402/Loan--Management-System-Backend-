# Loan Management System - Backend

A comprehensive microservices-based loan management system built with Spring Boot and Spring Cloud technologies. This system handles the complete loan lifecycle from application submission to payment processing, with robust analytics and notification capabilities.

## 🏗️ Architecture Overview

This project follows a microservices architecture pattern with the following key components:

### Core Services
- **API Gateway** - Single entry point for all client requests, handles routing and load balancing
- **Eureka Server** - Service discovery and registration
- **Config Server** - Centralized configuration management
- **Auth Service** - Authentication and authorization

### Business Services
- **Customer Service** - Manages customer information and profiles
- **Loan Application Service** - Handles loan application submissions and tracking
- **Loan Processing Service** - Processes loan applications, eligibility checks, and approvals
- **Payment Service** - Manages loan payments and EMI processing
- **Analytics Service** - Provides analytics and reporting capabilities
- **Notification Service** - Sends notifications to customers about loan status

## 🚀 Technologies Used

- **Java** (96.8%)
- **Spring Boot** - Core framework
- **Spring Cloud** - Microservices infrastructure
  - Spring Cloud Config
  - Spring Cloud Netflix Eureka
  - Spring Cloud Gateway
- **Maven** - Dependency management
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- Java Development Kit (JDK) 8 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- MySQL or PostgreSQL (for database)

## 🛠️ Installation & Setup

### Option 1: Using Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone https://github.com/Akankshya1402/Loan--Management-System-Backend-.git
cd Loan--Management-System-Backend-
```

2. Start all services using Docker Compose:
```bash
docker-compose up -d
```

### Option 2: Manual Setup

1. Clone the repository:
```bash
git clone https://github.com/Akankshya1402/Loan--Management-System-Backend-.git
cd Loan--Management-System-Backend-
```

2. Build the parent project:
```bash
mvn clean install
```

3. Start services in the following order:

#### For Windows:
```bash
start-all.bat
```

#### For PowerShell:
```powershell
.\start-all-services.ps1
```

#### Manual Service Startup:
```bash
# 1. Start Config Server
cd config-server
mvn spring-boot:run

# 2. Start Eureka Server
cd ../eureka-server
mvn spring-boot:run

# 3. Start other services (can be started in parallel)
cd ../auth-service
mvn spring-boot:run

cd ../customer-service
mvn spring-boot:run

cd ../loan-application-service
mvn spring-boot:run

cd ../loan-processing-service
mvn spring-boot:run

cd ../payment-service
mvn spring-boot:run

cd ../analytics-service
mvn spring-boot:run

cd ../notification-service
mvn spring-boot:run

# 4. Start API Gateway (last)
cd ../api-gateway
mvn spring-boot:run
```

## 🔌 Service Ports

| Service | Port |
|---------|------|
| Config Server | 8888 |
| Eureka Server | 8761 |
| API Gateway | 8080 |
| Auth Service | 8081 |
| Customer Service | 8082 |
| Loan Application Service | 8083 |
| Loan Processing Service | 8084 |
| Payment Service | 8085 |
| Analytics Service | 8086 |
| Notification Service | 8087 |

## 📊 Service Discovery

Access the Eureka Dashboard to view all registered services:
```
http://localhost:8761
```

## 🧪 Testing

### Using Postman

1. Import the provided Postman collection:
   - File: `loan-management-system.postman.json`

2. Run the collection using Newman (CLI):
```bash
newman run loan-management-system.postman.json
```

3. View Newman Report:
   - Report file available in the repository

## 📖 API Documentation

All API endpoints are accessible through the API Gateway at:
```
http://localhost:8080
```

### Sample Endpoints

- **Authentication**
  - `POST /api/auth/register` - Register new user
  - `POST /api/auth/login` - User login

- **Customer Management**
  - `GET /api/customers` - Get all customers
  - `POST /api/customers` - Create customer
  - `GET /api/customers/{id}` - Get customer by ID

- **Loan Application**
  - `POST /api/loans/apply` - Submit loan application
  - `GET /api/loans/{id}` - Get loan details
  - `GET /api/loans/customer/{customerId}` - Get customer loans

- **Loan Processing**
  - `PUT /api/loans/{id}/approve` - Approve loan
  - `PUT /api/loans/{id}/reject` - Reject loan

- **Payments**
  - `POST /api/payments` - Make payment
  - `GET /api/payments/loan/{loanId}` - Get loan payment history

## 🛑 Stopping Services

### For Windows:
```bash
stop-all.bat
```

### For PowerShell:
```powershell
.\stop-all-services.ps1
```

### For Docker:
```bash
docker-compose down
```

## 📁 Project Structure

```
Loan--Management-System-Backend-/
├── .settings/
├── analytics-service/
├── api-gateway/
├── auth-service/
├── config-server/
├── customer-service/
├── eureka-server/
├── loan-application-service/
├── loan-processing-service/
├── notification-service/
├── payment-service/
├── docker-compose.yml
├── pom.xml
├── start-all.bat
├── start-all-services.ps1
├── stop-all.bat
├── stop-all-services.ps1
├── loan-management-system.postman.json
├── Newman Report
├── Loan_Management_System_Diagrams.pdf
└── capstone project (lms).pdf
```

## 📚 Documentation

- **System Diagrams**: See `Loan_Management_System_Diagrams.pdf`
- **Project Documentation**: See `capstone project (lms).pdf`
- **API Testing Report**: See `Newman Report`

## 🔒 Security

- JWT-based authentication
- Role-based access control (RBAC)
- Secure API Gateway routing
- Service-to-service authentication

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👥 Contributors

- [Akankshya1402](https://github.com/Akankshya1402)
- [iAmTJ007](https://github.com/iAmTJ007)

## 📝 License

This project is available for educational and personal use.

## 📧 Contact

For questions or suggestions, please open an issue in the repository.

---

**Note**: Make sure to configure your database connections and other environment-specific settings in the Config Server before running the services.
