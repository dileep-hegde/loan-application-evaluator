# Loan Application Evaluator

A Spring Boot REST service for evaluating loan applications and determining loan offers based on applicant eligibility, risk assessment, and business rules.

## Features

- **Loan Application Evaluation**: Process loan applications with comprehensive eligibility checks.
- **Risk-Based Interest Rates**: Calculate interest rates based on credit score, employment type, and loan size.
- **EMI Calculation**: Accurate EMI calculation using the standard EMI formula with BigDecimal precision.
- **Single Offer Generation**: Generate a single loan offer based on the requested tenure when eligible.
- **Validation & Error Handling**: Comprehensive input validation with meaningful error messages and centralized exception handling.
- **Audit Trail**: Store all applications (approved and rejected) for audit purposes.

## Tech Stack

- Java 17
- Spring Boot 4.0.3
- Spring Web (REST)
- Spring Data JPA
- H2 (in-memory database)
- Maven
- JUnit 5 & Mockito (tests)

## Prerequisites

- Java 17 or higher
- Use the Maven wrapper in the repo

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/dileep-hegde/loan-application-evaluator
cd loan-application-evaluator
```

### 2. Build the Project

```powershell
./mvnw clean install
```

### 3. Run the Application

```powershell
./mvnw spring-boot:run
```

The application will start on: `http://localhost:8084`

### 4. Run Tests

```powershell
./mvnw test
```

## API

### Create Loan Application

**Endpoint**: `POST /applications`

**Request Body**:

```json
{
  "applicant": {
    "name": "John Doe",
    "age": 30,
    "monthlyIncome": 75000,
    "employmentType": "SALARIED",
    "creditScore": 720
  },
  "loan": {
    "amount": 500000,
    "tenureMonths": 36,
    "purpose": "PERSONAL"
  }
}
```

**Successful Response** (`201 Created`):

```json
{
  "applicationId": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
  "status": "APPROVED",
  "riskBand": "MEDIUM",
  "offer": {
    "interestRate": 13.5,
    "tenureMonths": 36,
    "emi": 16967.64,
    "totalPayable": 610835.04
  }
}
```

**Rejected Response** (`201 Created`):

```json
{
  "applicationId": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
  "status": "REJECTED",
  "riskBand": null,
  "rejectionReasons": [
    "CREDIT_SCORE_TOO_LOW",
    "EMI_EXCEEDS_60_PERCENT"
  ]
}
```

**Validation Error** (`400 Bad Request`):

```json
{
  "timestamp": "2026-03-04T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "messages": [
    "Maximum age must be 60",
    "Maximum Loan amount must be 5,000,000"
  ]
}
```

## Business Rules

### Validation Rules

- Age: 21–60 years
- Credit Score: 300–900
- Loan Amount: ₹10,000 – ₹50,00,000
- Tenure: 6–360 months
- Monthly Income: > 0

### Eligibility Rules

- Credit score must be ≥ 600.
- Age + Tenure (in years) must be ≤ 65.
- EMI must be ≤ 60% of monthly income.
- For offer approval: EMI must be ≤ 50% of monthly income.

### Risk Classification

- **LOW**: Credit score ≥ 750
- **MEDIUM**: Credit score 650–749
- **HIGH**: Credit score 600–649

### Interest Rate Calculation

Final Interest Rate:

> `Final Rate = Base Rate (12%) + Risk Premium + Employment Premium + Loan Size Premium`

**Risk Premium**:

- LOW: +0%
- MEDIUM: +1.5%
- HIGH: +3%

**Employment Premium**:

- SALARIED: +0%
- SELF_EMPLOYED: +1%

**Loan Size Premium**:

- Loan > ₹10,00,000: +0.5%
- Otherwise: +0%

## H2 Database

The application uses an in-memory H2 database.

When enabled in `application.properties`, you can access the H2 console at:

- `http://localhost:8080/h2-console`

Typical connection details:

- JDBC URL: `jdbc:h2:mem:loanauditdb`
- Username: `sa`
- Password: (empty, unless configured otherwise)

## Testing

The project includes tests that cover:

- EMI calculation logic with various scenarios.
- Risk classification boundary conditions.
- Eligibility checking and rejection reasons.
- Complete application processing flow (approved and rejected cases).

on Windows PowerShell:

```powershell
./mvnw test
```

## License

This project is for demonstration and educational purposes.
