package com.dileephegde.loanapplicationevaluator.entity;

import com.dileephegde.loanapplicationevaluator.entity.enums.ApplicationStatus;
import com.dileephegde.loanapplicationevaluator.entity.enums.RejectionReason;
import com.dileephegde.loanapplicationevaluator.entity.enums.RiskBand;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    private String applicationId;

    @Embedded
    private Applicant applicant;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "loan_amount")),
            @AttributeOverride(name = "tenureMonths", column = @Column(name = "loan_tenure_months")),
            @AttributeOverride(name = "purpose", column = @Column(name = "loan_purpose"))
    })
    private Loan loan;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    private RiskBand riskBand;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "interestRate", column = @Column(name = "offer_interest_rate")),
            @AttributeOverride(name = "tenureMonths", column = @Column(name = "offer_tenure_months")),
            @AttributeOverride(name = "emi", column = @Column(name = "offer_emi")),
            @AttributeOverride(name = "totalPayable", column = @Column(name = "offer_total_payable"))
    })
    private Offer offer;

    @ElementCollection
    @CollectionTable(name = "rejection_reasons", joinColumns = @JoinColumn(name = "application_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private List<RejectionReason> rejectionReasons;

    @PrePersist
    public void generateId() {
        if (this.applicationId == null) {
            this.applicationId = UUID.randomUUID().toString();
        }
        if (this.rejectionReasons == null) {
            this.rejectionReasons = new ArrayList<>();
        }
    }

    public LoanApplication() {
    }

    public LoanApplication(String applicationId, Applicant applicant, Loan loan, ApplicationStatus status, RiskBand riskBand, Offer offer, List<RejectionReason> rejectionReasons) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.loan = loan;
        this.status = status;
        this.riskBand = riskBand;
        this.offer = offer;
        this.rejectionReasons = rejectionReasons;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public RiskBand getRiskBand() {
        return riskBand;
    }

    public void setRiskBand(RiskBand riskBand) {
        this.riskBand = riskBand;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<RejectionReason> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplication that = (LoanApplication) o;
        return Objects.equals(applicationId, that.applicationId) && Objects.equals(applicant, that.applicant) && Objects.equals(loan, that.loan) && status == that.status && riskBand == that.riskBand && Objects.equals(offer, that.offer) && Objects.equals(rejectionReasons, that.rejectionReasons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId, applicant, loan, status, riskBand, offer, rejectionReasons);
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "applicationId='" + applicationId + '\'' +
                ", applicant=" + applicant +
                ", loan=" + loan +
                ", status=" + status +
                ", riskBand=" + riskBand +
                ", offer=" + offer +
                ", rejectionReasons=" + rejectionReasons +
                '}';
    }

    public static LoanApplicationBuilder builder() {
        return new LoanApplicationBuilder();
    }

    public static class LoanApplicationBuilder {
        private String applicationId;
        private Applicant applicant;
        private Loan loan;
        private ApplicationStatus status;
        private RiskBand riskBand;
        private Offer offer;
        private List<RejectionReason> rejectionReasons;

        public LoanApplicationBuilder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public LoanApplicationBuilder applicant(Applicant applicant) {
            this.applicant = applicant;
            return this;
        }

        public LoanApplicationBuilder loan(Loan loan) {
            this.loan = loan;
            return this;
        }

        public LoanApplicationBuilder status(ApplicationStatus status) {
            this.status = status;
            return this;
        }

        public LoanApplicationBuilder riskBand(RiskBand riskBand) {
            this.riskBand = riskBand;
            return this;
        }

        public LoanApplicationBuilder offer(Offer offer) {
            this.offer = offer;
            return this;
        }

        public LoanApplicationBuilder rejectionReasons(List<RejectionReason> rejectionReasons) {
            this.rejectionReasons = rejectionReasons;
            return this;
        }

        public LoanApplication build() {
            return new LoanApplication(applicationId, applicant, loan, status, riskBand, offer, rejectionReasons);
        }
    }
}
