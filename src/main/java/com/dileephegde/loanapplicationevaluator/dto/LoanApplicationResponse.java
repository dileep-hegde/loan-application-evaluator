package com.dileephegde.loanapplicationevaluator.dto;

import com.dileephegde.loanapplicationevaluator.entity.enums.ApplicationStatus;
import com.dileephegde.loanapplicationevaluator.entity.enums.RejectionReason;
import com.dileephegde.loanapplicationevaluator.entity.enums.RiskBand;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record LoanApplicationResponse(
        String applicationId,
        ApplicationStatus status,
        RiskBand riskBand,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        OfferDTO offer,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<RejectionReason>rejectionReasons
){}
