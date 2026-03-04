package com.dileephegde.loanapplicationevaluator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LoanApplicationRequest(

        @NotNull(message = "Applicant details are required")
        @Valid
        ApplicantDTO applicant,

        @NotNull(message = "Loan details are required")
        @Valid
        LoanDTO loan
){}
