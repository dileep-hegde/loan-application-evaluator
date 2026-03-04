package com.dileephegde.loanapplicationevaluator.service;

import com.dileephegde.loanapplicationevaluator.dto.*;
import com.dileephegde.loanapplicationevaluator.entity.Applicant;
import com.dileephegde.loanapplicationevaluator.entity.Loan;
import com.dileephegde.loanapplicationevaluator.entity.LoanApplication;
import com.dileephegde.loanapplicationevaluator.entity.Offer;
import com.dileephegde.loanapplicationevaluator.entity.enums.ApplicationStatus;
import com.dileephegde.loanapplicationevaluator.entity.enums.EmploymentType;
import com.dileephegde.loanapplicationevaluator.entity.enums.RejectionReason;
import com.dileephegde.loanapplicationevaluator.entity.enums.RiskBand;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationService {

    public LoanApplicationResponse processLoanApplication(LoanApplicationRequest request) {
        ApplicantDTO applicantDTO = request.applicant();
        LoanDTO loanDTO = request.loan();

        RiskBand riskBand = classifyRiskBand(applicantDTO.creditScore());

        BigDecimal interestRate = calculateInterestRate(riskBand, applicantDTO.employmentType(), loanDTO.amount());

        return null;
    }

    private BigDecimal calculateInterestRate(RiskBand riskBand, EmploymentType employmentType, BigDecimal loanAmount) {
        BigDecimal baseRate = new BigDecimal("12.0");
        BigDecimal riskPremium = switch (riskBand) {
            case LOW -> BigDecimal.ZERO;
            case MEDIUM -> new BigDecimal("1.5");
            case HIGH -> new BigDecimal("3.0");
        };

        BigDecimal employmentPremium = switch (employmentType) {
            case SALARIED -> BigDecimal.ZERO;
            case SELF_EMPLOYED -> new BigDecimal("1.0");
        };

        BigDecimal loanSizePremium;
        BigDecimal threshold = new BigDecimal("1000000");
        if (loanAmount.compareTo(threshold) > 0) {
            loanSizePremium = new BigDecimal("0.5");
        } else {
            loanSizePremium = BigDecimal.ZERO;
        }

        return baseRate.add(riskPremium).add(employmentPremium).add(loanSizePremium);
    }

    private RiskBand classifyRiskBand(Integer creditScore) {
        if (creditScore >= 750) {
            return RiskBand.LOW;
        } else if (creditScore >= 650) {
            return RiskBand.MEDIUM;
        } else {
            return RiskBand.HIGH;
        }
    }

    private Loan mapToLoan(LoanDTO dto){
        return new Loan(dto.amount(), dto.tenureMonths(), dto.purpose());
    }

    private Applicant mapToApplicant(ApplicantDTO dto){
        return new Applicant(dto.name(), dto.age(), dto.monthlyIncome(), dto.employmentType(), dto.creditScore());
    }

    private OfferDTO mapToOfferDTO(Offer offer) {
        if (offer == null) {
            return null;
        }
        return new OfferDTO(offer.interestRate(), offer.tenureMonths(), offer.emi(), offer.totalPayable());
    }

    private LoanApplicationResponse mapToResponse(LoanApplication application) {
        if (application == null) {
            return null;
        }

        LoanApplicationResponse loanApplicationResponse;
        OfferDTO offerDTO = null;
        List<RejectionReason> rejectionReason = new ArrayList<>();

        if (application.getStatus() == ApplicationStatus.APPROVED) {
            offerDTO = mapToOfferDTO(application.getOffer());
        } else {
            rejectionReason.addAll(application.getRejectionReasons());
        }

        loanApplicationResponse = new LoanApplicationResponse(
                application.getApplicationId(),
                application.getStatus(),
                application.getRiskBand(),
                offerDTO,
                rejectionReason
        );

        return loanApplicationResponse;
    }
}
