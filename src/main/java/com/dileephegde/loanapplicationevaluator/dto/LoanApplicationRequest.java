package com.dileephegde.loanapplicationevaluator.dto;

public record LoanApplicationRequest(
        ApplicantDTO applicant,
        LoanDTO loan
){}
