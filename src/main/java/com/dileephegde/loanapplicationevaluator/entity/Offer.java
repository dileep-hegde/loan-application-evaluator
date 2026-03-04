package com.dileephegde.loanapplicationevaluator.entity;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record Offer(
        BigDecimal interestRate,
        Integer tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable
){}
