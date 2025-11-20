# Connect Money API 💰

A secure RESTful API for managing financial transactions with OAuth2-style authentication, rate limiting, and idempotency support.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Security Features](#security-features)
- [Project Structure](#project-structure)
- [Testing](#testing)

## 🎯 Overview

Connect Money API is a Spring Boot-based financial transaction management system that provides secure endpoints for processing payments, transfers, and withdrawals. The API implements industry-standard security practices including JWT authentication, rate limiting, account lockout mechanisms, and idempotency keys to prevent duplicate transactions.

## ✨ Features

- **OAuth2-style Token Authentication**: Client credentials flow for secure API access
- **JWT-based Authorization**: Stateless authentication using JSON Web Tokens
- **Rate Limiting**: Bucket4j-based rate limiting to prevent API abuse
- **Account Lockout**: Automatic account lockout after failed authentication attempts
- **Idempotency**: Prevent duplicate transactions using idempotency keys
- **Database Migrations**: Flyway for version-controlled database schema management
- **Request Validation**: Comprehensive input validation using Bean Validation
- **Global Exception Handling**: Centralized error handling with structured error responses
- **CORS Support**: Configurable Cross-Origin Resource Sharing
- **Security Headers**: XSS protection, Content Security Policy, and more

## 🛠 Technologies

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **MySQL 9.3** - Database
- **Flyway** - Database migrations
- **JWT (jjwt)** - Token generation and validation
- **Bucket4j** - Rate limiting
- **Lombok** - Boilerplate code reduction
- **Maven** - Dependency management

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- Java 17 or higher
- Maven 3.6+ (or use Maven Wrapper included in project)
- MySQL 9.3 or compatible version
- Git (optional, for cloning the repository)

## 🔧 Installation

1. **Clone the repository** (or download the project):
   ```bash
   git clone <repository-url>
   cd Connect_Money_API
   ```

2. **Set up MySQL database**:
   - Create a MySQL database named `connectmoney` (or update the configuration)
   - Ensure MySQL is running on `localhost:3306`

3. **Update database credentials** in `src/main/resources/application.yaml`:
   ```yaml
   spring:
     datasource:
       username: your_username
       password: your_password
   ```

## ⚙️ Configuration

The application configuration is in `src/main/resources/application.yaml`:

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/connectmoney?createDatabaseIfNotExist=true
    username: root
    password: root
```

### Security Configuration
```yaml
security:
  jwt:
    secret_key: "your-secret-key-here"
    expiration_time: 3600000  # 1 hour in milliseconds
  client-credentials:
    max-number-of-attempts: 5
    lockout-duration-time: 300  # 5 minutes in seconds
```

### Rate Limiting
```yaml
security:
  rate-limit:
    capacity: 100  # Maximum requests
    refill-tokens: 10  # Tokens added per refill
    refill-duration: 60  # Refill interval in seconds
```

### Server Configuration
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: moneyy
    key-store-type: PKCS12
    key-alias: connectmoney
```

- **Port**: 8443 (HTTPS)
- **SSL**: Enabled by default using PKCS12 keystore
- **Protocol**: HTTPS/TLS encrypted connections

### SSL/TLS Configuration

The application uses SSL/TLS encryption for secure connections. The SSL configuration includes:

- **Keystore**: PKCS12 format keystore (`keystore.p12`)
- **Keystore Password**: `moneyy`
- **Key Alias**: `connectmoney`
- **Certificate**: Self-signed certificate (for development)

**Note**: For production, replace the self-signed certificate with a certificate from a trusted Certificate Authority (CA).

**Important**: Make sure the `keystore.p12` file exists in `src/main/resources/` directory. If you don't have a keystore, you can generate one using:

```bash
keytool -genkeypair -alias connectmoney -keyalg RSA -keysize 2048 -storetype PKCS12 \
  -keystore src/main/resources/keystore.p12 -validity 365 -storepass moneyy
```

## 🗄 Database Setup

The application uses Flyway for database migrations. On first run, Flyway will:

1. Create the database schema (tables: `client`, `transactions`, `idempotency_keys`)
2. Insert sample data (test clients and transactions)

### Sample Credentials

After running migrations, you can use these test credentials:

| Client ID | Client Secret | Status |
|-----------|---------------|--------|
| `test_client` | `SecurePassword123!` | Active |
| `demo_client` | `DemoPassword456!` | Active |

**Note**: The actual client secrets are stored as BCrypt hashes in the database. The plain text passwords above are for reference only.

### Manual Migration (Optional)

To run Flyway migrations manually:
```bash
./mvnw flyway:migrate
```

To repair Flyway schema history (if needed):
```bash
./mvnw flyway:repair
```

## 🚀 Running the Application

### Using Maven Wrapper (Recommended)
```bash
./mvnw spring-boot:run
```

### Using Maven (if installed)
```bash
mvn spring-boot:run
```

### Using IDE
1. Import the project as a Maven project
2. Run `ConnectMoneyApiApplication.java` as a Spring Boot application

The API will be available at: `https://localhost:8443`

**Note**: Since a self-signed certificate is used, browsers and tools like curl may show SSL certificate warnings. You can bypass this in curl using the `-k` or `--insecure` flag for development purposes.

## 📚 API Documentation

### Base URL
```
https://localhost:8443/v1
```

### Authentication Endpoint

#### Get Access Token

**Endpoint:** `POST /v1/protocol/openid-connect/token`

**Content-Type:** `application/x-www-form-urlencoded`

**Request Body (form-data):**
```
grant_type=client_credentials
client_id=test_client
client_secret=SecurePassword123!
```

**Success Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600,
  "token_type": "Bearer"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2025-11-20T16:23:36",
  "status": 401,
  "error": "Authentication failed please try again!"
}
```

### Transaction Endpoint

#### Create Transaction

**Endpoint:** `POST /v1/transactions`

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
x-idempotency-key: <unique-key>
```

**Request Body:**
```json
{
  "id": "txn_123456789",
  "type": "TRANSFER",
  "status": "COMPLETED",
  "amount": 100.50,
  "currency": "EGP",
  "cardUid": "100003145552",
  "createdAt": "2025-11-20"
}
```

**Success Response (200 OK):**
```
No content body
```

**Error Responses:**

*400 Bad Request* (Validation Error):
```json
{
  "time": "2025-11-20T16:23:36",
  "status": 400,
  "errors": {
    "amount": "Amount must be greater than 0",
    "currency": "Currency is required"
  }
}
```

*401 Unauthorized* (Invalid/Missing Token):
```json
{
  "timestamp": "2025-11-20T16:23:36",
  "status": 401,
  "error": "Authentication failed please try again!"
}
```

*429 Too Many Requests* (Rate Limit Exceeded):
```
HTTP Status: 429
```

## 🔐 Security Features

### Authentication & Authorization
- **Client Credentials Flow**: OAuth2-style token endpoint
- **JWT Tokens**: Secure, stateless authentication
- **Account Lockout**: Automatic lockout after 5 failed attempts for 5 minutes
- **Password Hashing**: BCrypt with strength factor of 12

### Rate Limiting
- **Token Bucket Algorithm**: Bucket4j implementation
- **Configurable Limits**: 100 requests capacity, 10 tokens per 60 seconds
- **Per-Client Limiting**: Rate limits applied per client

### Security Headers
- **XSS Protection**: Enabled
- **Content Security Policy**: Configured
- **Frame Options**: Denied (prevents clickjacking)
- **Content Type Options**: nosniff

### CORS
- **Allowed Origins**: Configurable (default: `https://AyDomainDelwaqty.com`)
- **Allowed Methods**: GET, POST
- **Allowed Headers**: Authorization, Content-Type, x-idempotency-key

### Idempotency
- Prevents duplicate transaction processing
- Uses unique idempotency keys per request
- Stored in database for verification

## 📁 Project Structure

```
Connect_Money_API/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/Connect_Money_API/
│   │   │       ├── controller/          # REST controllers
│   │   │       │   ├── AuthController.java
│   │   │       │   └── TransactionController.java
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       │   ├── TokenRequest.java
│   │   │       │   ├── TokenResponse.java
│   │   │       │   └── TransactionRequest.java
│   │   │       ├── exception/           # Exception handlers
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       ├── model/               # JPA entities
│   │   │       │   ├── Client.java
│   │   │       │   ├── Transaction.java
│   │   │       │   └── IdempotencyKey.java
│   │   │       ├── repository/          # Data repositories
│   │   │       │   ├── ClientRepository.java
│   │   │       │   ├── TransactionRepository.java
│   │   │       │   └── IdempotencyKeyRepository.java
│   │   │       ├── security/            # Security configuration
│   │   │       │   ├── AuthFilter.java
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── RateLimitFilter.java
│   │   │       │   └── SecurityConfig.java
│   │   │       └── Service/             # Business logic
│   │   │           ├── AuthService.java
│   │   │           └── TransactionService.java
│   │   └── resources/
│   │       ├── application.yaml         # Application configuration
│   │       └── db/
│   │           └── migration/           # Flyway migrations
│   │               ├── V1__Initial_Schema.sql
│   │               └── V2__Insert_Sample_Data.sql
│   └── test/                            # Test classes
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## 🧪 Testing

### Using Postman/curl

#### 1. Get Access Token
```bash
curl -k -X POST https://localhost:8443/v1/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=test_client" \
  -d "client_secret=SecurePassword123!"
```

**Note**: The `-k` flag is used to bypass SSL certificate verification for self-signed certificates in development. Remove this flag in production with a valid CA certificate.

#### 2. Create Transaction
```bash
curl -k -X POST https://localhost:8443/v1/transactions \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -H "x-idempotency-key: unique-key-123" \
  -d '{
    "id": "txn_123456789",
    "type": "TRANSFER",
    "status": "COMPLETED",
    "amount": 100.50,
    "currency": "EGP",
    "cardUid": "100003145552",
    "createdAt": "2025-11-20"
  }'
```

### Postman Collection

1. Import the endpoints into Postman
2. Set environment variables:
   - `base_url`: `https://localhost:8443/v1`
   - `client_id`: `test_client`
   - `client_secret`: `SecurePassword123!`

**Note**: When using Postman with HTTPS and self-signed certificates:
- Go to Settings → SSL certificate verification → Toggle OFF (for development only)
- Or import the certificate into Postman's certificate manager

## 📝 Transaction Types

Supported transaction types:
- `TRANSFER` - Transfer between accounts
- `PAYMENT` - Payment transaction
- `WITHDRAWAL` - Cash withdrawal

## 📝 Transaction Statuses

Supported transaction statuses:
- `COMPLETED` - Transaction successfully completed
- `PENDING` - Transaction pending processing
- `FAILED` - Transaction failed

## ⚠️ Important Notes

1. **Client Secrets**: In production, never hardcode client secrets. Use environment variables or secure vaults.

2. **JWT Secret Key**: Change the `secret_key` in `application.yaml` for production use.

3. **Database Passwords**: Update database credentials before deploying to production.

4. **SSL/TLS**: SSL is enabled by default. For production, replace the self-signed certificate with a valid CA-signed certificate.

5. **Rate Limits**: Adjust rate limiting based on your application's requirements.

6. **Idempotency Keys**: Must be unique per transaction. Reusing a key will return the original response without processing.

## 🐛 Troubleshooting

### Common Issues

**Issue**: `Flyway migration failed`
- **Solution**: Run `./mvnw flyway:repair` to fix schema history

**Issue**: `Authentication failed`
- **Solution**: Verify client credentials match the database. Check if account is locked.

**Issue**: `Rate limit exceeded`
- **Solution**: Wait for the rate limit window to reset or adjust limits in configuration

**Issue**: `Connection refused`
- **Solution**: Ensure MySQL is running and credentials are correct

**Issue**: `SSL certificate verification failed` or `certificate verify failed`
- **Solution**: This is expected with self-signed certificates. Use `-k` flag in curl or disable SSL verification in Postman settings (development only). For production, use a valid CA-signed certificate.

**Issue**: `keystore.p12 not found`
- **Solution**: Generate a keystore using the keytool command provided in the SSL Configuration section above.

## 📄 License

This project is for educational/demonstration purposes.

## 👥 Author

Eng. Omar Mohamed.


