package com.dileephegde.loanapplicationevaluator.service;

import com.dileephegde.loanapplicationevaluator.dto.ApplicantDTO;
import com.dileephegde.loanapplicationevaluator.dto.LoanApplicationResponse;
import com.dileephegde.loanapplicationevaluator.dto.LoanDTO;
import com.dileephegde.loanapplicationevaluator.dto.OfferDTO;
import com.dileephegde.loanapplicationevaluator.entity.Applicant;
import com.dileephegde.loanapplicationevaluator.entity.Loan;
import com.dileephegde.loanapplicationevaluator.entity.LoanApplication;
import com.dileephegde.loanapplicationevaluator.entity.Offer;
import com.dileephegde.loanapplicationevaluator.entity.enums.ApplicationStatus;
import com.dileephegde.loanapplicationevaluator.entity.enums.RejectionReason;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationService {

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
