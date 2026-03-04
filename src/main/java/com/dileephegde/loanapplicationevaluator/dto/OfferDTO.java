package com.dileephegde.loanapplicationevaluator.dto;

import java.math.BigDecimal;

public record OfferDTO(
        BigDecimal interestRate,
        Integer tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable
){}
