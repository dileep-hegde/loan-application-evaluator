package com.dileephegde.loanapplicationevaluator.dto;

import com.dileephegde.loanapplicationevaluator.entity.enums.LoanPurpose;

import java.math.BigDecimal;

public record LoanDTO(
        BigDecimal amount,
        Integer tenureMonths,
        LoanPurpose purpose
) {
}
