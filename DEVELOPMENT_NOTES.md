# Development Notes

## Overall approach

This project implements a Spring Boot REST service for evaluating loan applications. 

The implementation follows a **layered architecture** with clear separation of concerns:

1. **Controller Layer**
2. **Service Layer**
3. **Entity/Domain Layer**
4. **Repository Layer**
5. **DTO Layer**

The development followed an **incremental approach**, building one layer at a time with logical commits representing each development step.

## Key design decisions

### 1. Use of Records for DTOs, Embeddable Value Objects and Error Responses
Records are used for DTOs, embedded value objects, and error responses.

#### Why Records over traditional classes?
- Concise syntax reduces boilerplate code for getters, constructors, equals, hashCode, and toString
- Immutability by default, which is ideal for DTOs and value objects
- Improves readability and maintainability by clearly indicating these are simple data carriers
- Applicant, Loan, and Offer are modeled as @Embeddable value objects within LoanApplication, which simplifies the data model and eliminates unnecessary joins.

### 2. BigDecimal for Financial Calculations 
All monetary values and interest rates use `BigDecimal` with explicit scale and rounding mode.

#### Why BigDecimal?
- Avoids floating-point precision errors that are unacceptable in financial calculations
- Meets the requirement for scale = 2 and HALF_UP rounding
- Industry best practice for financial applications

### 3. Enum-Based Rejection Reasons

Rejection reasons are modeled as enums rather than strings.

#### What are the benefits of using enums for rejection reasons?
- Type-safe and prevents typos
- Can include descriptions for better error messages
- Easy to extend with new reasons 

### 4. In-Memory H2 Database

Used H2 for data persistence instead of external database.

#### Why H2 over PostgreSQL/MySQL?
- Simplifies setup and testing
- Suitable for development and demonstration
- Easy to switch to production database (PostgreSQL, MySQL) by changing configuration
- Includes audit trail by storing all applications

### 5. Validation at DTO Level

Validation annotations are applied to DTOs rather than domain entities.

#### Why validate at the DTO level?
- Separates API validation concerns from domain logic
- Allows different validation rules for different API endpoints if needed
- Domain entities remain focused on business rules

## Trade-offs considered

### 1. Embedded Entities vs. Separate Tables

**Decision**: Use embedded entities for Applicant, Loan, Offer

**Trade-off**:
- Pro: Simpler queries, better performance, clear lifecycle
- Con: Cannot query applicants/loans independently
- This is acceptable given the requirements focus on applications, not standalone entities

### 2. Records over Lombok

**Decision**: Use Java records instead of Lombok for DTOs and value objects

**Trade-off**:
- Pro: No additional dependencies, better readability, immutability by default
- Con: Records cannot have mutable fields or no-arg constructors, but this is not needed
- Overall, the benefits of records outweigh the limitations for this use case

### 3. Manual Builder Pattern vs. Lombok @Builder

**Decision**: Implement manual builder pattern for LoanApplication entity instead of using Lombok's @Builder

**Trade-off**:
- Pro: Full control over the builder implementation, no additional dependencies, better readability
- Con: More boilerplate code compared to Lombok's @Builder, but this is acceptable
- The manual builder allows for more complex construction logic if needed in the future, which is a potential advantage over Lombok's @Builder.

## Assumptions made

1. **Loan Tenure**: The tenure is provided in months and is a positive integer. The service does not handle partial months or years.
2. **Database Selection**:  The service will be used as a demonstration and development exercise, so an in-memory database is sufficient for persistence.
3. **Application Security**: The service will not be exposed to public, so authentication and authorization are not required for this implementation.
4. **Loan Size Premium Threshold**: "Loan > 10,00,000" is interpreted as strictly greater than (not ≥)
5. **Persistence**: All applications (approved and rejected) are persisted for audit purposes
6. **Error Handling**: Validation errors return 400 Bad Request with detailed field-level error messages
7. **Concurrent Requests**: Application is thread-safe but doesn't handle distributed transactions (acceptable for current scale)

## Improvements you would make with more time

### 1. API Documentation
- Add Swagger/OpenAPI documentation
- Include example requests/responses
- Document all validation rules and error codes

### 2. Logging and Monitoring
- Structured logging with correlation IDs
- Metrics collection (application success rate, processing time

### 3. Security Enhancements
- Add authentication/authorization (OAuth2, JWT)
- Rate limiting to prevent abuse
- Input sanitization for additional security
- Audit logging for compliance

### 4. Separation of Service Responsibilities
- Refactor the LoanApplicationService into smaller, more focused services (e.g., EmiCalculator, RiskClassifier, InterestRateCalculator, EligibilityChecker) to improve maintainability and testability.
- This would allow for better separation of concerns and make it easier to extend or modify specific aspects of the loan evaluation process in the future.
- While this would increase the number of classes and slightly complicate orchestration, the benefits in terms of maintainability and testability would outweigh the drawbacks for a business-critical application.
- This would also allow for more targeted unit tests for each specific service, improving overall test coverage and reliability.

## Conclusion

This implementation provides a **solid foundation** for a loan application evaluator service:

- Clean, maintainable code structure
- Comprehensive business logic coverage
- Robust validation and error handling
- Extensive unit tests
- Clear commit history showing incremental development