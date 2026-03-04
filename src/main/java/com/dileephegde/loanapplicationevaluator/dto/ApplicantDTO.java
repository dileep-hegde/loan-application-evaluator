package com.dileephegde.loanapplicationevaluator.dto;

import com.dileephegde.loanapplicationevaluator.entity.enums.EmploymentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ApplicantDTO (
        @NotBlank(message = "Name is required")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Only letters are allowed for applicant name")
        String name,

        @NotNull(message = "Age is required")
        @Min(value = 21, message = "Minimum age must be 21")
        @Max(value = 60, message = "Maximum age must be 60")
        Integer age,

        @NotNull(message = "Monthly income is required")
        @DecimalMin(value = "0.01", message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,

        @NotNull(message = "Credit score is required")
        @Min(value = 300, message = "Minimum credit score must be 300")
        @Max(value = 900, message = "Maximum credit score must be 900")
        Integer creditScore
){}
