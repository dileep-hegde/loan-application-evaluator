package com.dileephegde.loanapplicationevaluator.dto;

import com.dileephegde.loanapplicationevaluator.entity.enums.ApplicationStatus;
import com.dileephegde.loanapplicationevaluator.entity.enums.RejectionReason;
import com.dileephegde.loanapplicationevaluator.entity.enums.RiskBand;

import java.util.List;

public record LoanApplicationResponse(
        String applicationId,
        ApplicationStatus status,
        RiskBand riskBand,
        OfferDTO offer,
        List<RejectionReason>rejectionReasons
){}
