package com.dileephegde.loanapplicationevaluator.entity;

import com.dileephegde.loanapplicationevaluator.entity.enums.LoanPurpose;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

public record Loan(
        BigDecimal amount,
        Integer tenureMonths,

        @Enumerated(EnumType.STRING)
        LoanPurpose purpose
){}
