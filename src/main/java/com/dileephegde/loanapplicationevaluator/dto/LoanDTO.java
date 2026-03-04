package com.dileephegde.loanapplicationevaluator.dto;

import com.dileephegde.loanapplicationevaluator.entity.enums.LoanPurpose;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LoanDTO(

        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "10000", message = "Minimum Loan amount must be 10,000")
        @DecimalMax(value = "5000000", message = "Maximum Loan amount must be 5,000,000")
        BigDecimal amount,

        @NotNull(message = "Tenure is required")
        @Min(value = 6, message = "Minimum Tenure must be 6 months")
        @Max(value = 360, message = "Maximum Tenure must be 360 months")
        Integer tenureMonths,

        @NotNull(message = "Loan purpose is required")
        LoanPurpose purpose
){}
