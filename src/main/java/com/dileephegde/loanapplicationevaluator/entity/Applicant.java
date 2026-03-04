package com.dileephegde.loanapplicationevaluator.entity;

import com.dileephegde.loanapplicationevaluator.entity.enums.EmploymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

public record Applicant (
        String name,
        Integer age,
        BigDecimal monthlyIncome,

        @Enumerated(EnumType.STRING)
        EmploymentType employmentType,
        Integer creditScore
){}
