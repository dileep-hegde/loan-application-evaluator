package com.dileephegde.loanapplicationevaluator.dto;

import com.dileephegde.loanapplicationevaluator.entity.enums.EmploymentType;

import java.math.BigDecimal;

public record ApplicantDTO (
    String name,
    Integer age,
    BigDecimal monthlyIncome,
    EmploymentType employmentType,
    Integer creditScore
){}
