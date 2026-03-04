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
import com.dileephegde.loanapplicationevaluator.repository.LoanApplicationRepository;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationService {
    private final LoanApplicationRepository loanApplicationRepository;

    public LoanApplicationService(LoanApplicationRepository loanApplicationRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
    }
    
    public LoanApplicationResponse processLoanApplication(LoanApplicationRequest request) {
        ApplicantDTO applicantDTO = request.applicant();
        LoanDTO loanDTO = request.loan();

        RiskBand riskBand = classifyRiskBand(applicantDTO.creditScore());
        BigDecimal interestRate = calculateInterestRate(riskBand, applicantDTO.employmentType(), loanDTO.amount());
        BigDecimal emi = calculateEmi(loanDTO.amount(), interestRate, loanDTO.tenureMonths());

        List<RejectionReason>  rejectionReasons = checkEligibility(applicantDTO.creditScore(), applicantDTO.age(), loanDTO.tenureMonths(), emi, applicantDTO.monthlyIncome());

        LoanApplication application = LoanApplication.builder()
                .applicant(mapToApplicant(applicantDTO))
                .loan(mapToLoan(loanDTO))
                .riskBand(riskBand)
                .build();

        if (!rejectionReasons.isEmpty()) {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRejectionReasons(rejectionReasons);
            application.setRiskBand(null);
            application.setOffer(null);
        } else {
            boolean isOfferValid = isOfferValid(emi, applicantDTO.monthlyIncome());

            if (!isOfferValid) {
                application.setStatus(ApplicationStatus.REJECTED);
                application.setRejectionReasons(List.of(RejectionReason.EMI_EXCEEDS_50_PERCENT));
                application.setOffer(null);
                application.setRiskBand(null);
            } else {
                BigDecimal totalPayable = calculateTotalPayable(emi, loanDTO.tenureMonths());

                Offer offer = new Offer(interestRate, loanDTO.tenureMonths(), emi, totalPayable);
                application.setStatus(ApplicationStatus.APPROVED);
                application.setOffer(offer);
            }
        }

        application = loanApplicationRepository.save(application);
        return mapToResponse(application);
    }

    private BigDecimal calculateTotalPayable(BigDecimal emi, Integer tenureMonths) {
        return emi.multiply(BigDecimal.valueOf(tenureMonths)).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isOfferValid(BigDecimal emi, BigDecimal monthlyIncome) {
        BigDecimal maxEmi = monthlyIncome.multiply(new BigDecimal("0.50")).setScale(2, java.math.RoundingMode.HALF_UP);
        return emi.compareTo(maxEmi) <= 0;
    }

    private List<RejectionReason> checkEligibility(Integer creditScore, Integer age, Integer tenureMonths, BigDecimal emi, BigDecimal monthlyIncome) {
            List<RejectionReason> rejectionReasons = new ArrayList<>();

            // Check credit score
            if (creditScore < 600) {
                rejectionReasons.add(RejectionReason.CREDIT_SCORE_TOO_LOW);
            }

            // Check age + tenure limit
            int tenureYears = (int) Math.ceil(tenureMonths / 12.0);
            if (age + tenureYears > 65) {
                rejectionReasons.add(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED);
            }

            // Check EMI vs income ratio (60% rule for initial eligibility)
            BigDecimal maxEmi = monthlyIncome
                    .multiply(new BigDecimal("0.60"))
                    .setScale(2, RoundingMode.HALF_UP);

            if (emi.compareTo(maxEmi) > 0) {
                rejectionReasons.add(RejectionReason.EMI_EXCEEDS_60_PERCENT);
            }

        return rejectionReasons;
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualInterestRate, int tenureMonths) {
        BigDecimal monthlyRate = annualInterestRate
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowerN = onePlusR.pow(tenureMonths);
        BigDecimal numerator = principal
                .multiply(monthlyRate)
                .multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
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
