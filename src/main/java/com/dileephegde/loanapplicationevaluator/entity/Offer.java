package com.dileephegde.loanapplicationevaluator.entity;

import java.math.BigDecimal;

public record Offer(
        BigDecimal interestRate,
        Integer tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable
){}
