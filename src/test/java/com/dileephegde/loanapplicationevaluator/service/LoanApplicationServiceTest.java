package com.dileephegde.loanapplicationevaluator.service;

import com.dileephegde.loanapplicationevaluator.dto.*;
import com.dileephegde.loanapplicationevaluator.entity.LoanApplication;
import com.dileephegde.loanapplicationevaluator.entity.enums.*;
import com.dileephegde.loanapplicationevaluator.repository.ILoanApplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationServiceTest {

    @Mock
    private ILoanApplicationRepository repository;

    @InjectMocks
    private LoanApplicationService service;

    @Nested
    class RiskClassificationTests {

        @Test
        void testRiskClassification_LowRisk() {
            ApplicantDTO applicant = createApplicant("John", 30, new BigDecimal("50000"), EmploymentType.SALARIED, 800);
            LoanDTO loan = createLoan(new BigDecimal("500000"), 120, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(RiskBand.LOW, response.riskBand());
            assertEquals(ApplicationStatus.APPROVED, response.status());
            verify(repository).save(any(LoanApplication.class));
        }

        @Test
        void testRiskClassification_MediumRisk() {
            ApplicantDTO applicant = createApplicant("Jane", 35, new BigDecimal("60000"), EmploymentType.SALARIED, 700);
            LoanDTO loan = createLoan(new BigDecimal("400000"), 84, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(RiskBand.MEDIUM, response.riskBand());
            assertEquals(ApplicationStatus.APPROVED, response.status());
            verify(repository).save(any(LoanApplication.class));
        }

        @Test
        void testRiskClassification_HighRisk() {
            ApplicantDTO applicant = createApplicant("Bob", 28, new BigDecimal("80000"), EmploymentType.SALARIED, 620);
            LoanDTO loan = createLoan(new BigDecimal("300000"), 60, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(RiskBand.HIGH, response.riskBand());
            assertEquals(ApplicationStatus.APPROVED, response.status());
            verify(repository).save(any(LoanApplication.class));
        }
    }

    @Nested
    class EmiCalculationTests {

        @Test
        void testEmiCalculation_Standard() {
            ApplicantDTO applicant = createApplicant("Alice", 30, new BigDecimal("50000"), EmploymentType.SALARIED, 800);
            LoanDTO loan = createLoan(new BigDecimal("500000"), 60, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            BigDecimal expectedEmi = new BigDecimal("11122.22");
            BigDecimal actualEmi = response.offer().emi();
            assertEquals(0, expectedEmi.compareTo(actualEmi), "EMI should be " + expectedEmi + " but was " + actualEmi);
        }

        @Test
        void testEmiCalculation_MediumRisk() {
            ApplicantDTO applicant = createApplicant("Charlie", 40, new BigDecimal("60000"), EmploymentType.SALARIED, 700);
            LoanDTO loan = createLoan(new BigDecimal("300000"), 36, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            BigDecimal expectedEmi = new BigDecimal("10180.59");
            BigDecimal actualEmi = response.offer().emi();
            assertEquals(0, expectedEmi.compareTo(actualEmi), "EMI should be " + expectedEmi + " but was " + actualEmi);
        }

        @Test
        void testEmiCalculation_SelfEmployed() {
            ApplicantDTO applicant = createApplicant("David", 35, new BigDecimal("70000"), EmploymentType.SELF_EMPLOYED, 800);
            LoanDTO loan = createLoan(new BigDecimal("400000"), 48, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            BigDecimal expectedEmi = new BigDecimal("10731.00");
            BigDecimal actualEmi = response.offer().emi();
            assertEquals(0, expectedEmi.compareTo(actualEmi), "EMI should be " + expectedEmi + " but was " + actualEmi);
        }

        @Test
        void testEmiCalculation_LargeLoan() {
            ApplicantDTO applicant = createApplicant("Emma", 30, new BigDecimal("100000"), EmploymentType.SALARIED, 800);
            LoanDTO loan = createLoan(new BigDecimal("1500000"), 120, LoanPurpose.HOME);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
            LoanApplicationResponse response = service.processLoanApplication(request);
            BigDecimal expectedEmi = new BigDecimal("21956.43");
            BigDecimal actualEmi = response.offer().emi();
            assertEquals(0, expectedEmi.compareTo(actualEmi), "EMI should be " + expectedEmi + " but was " + actualEmi);
        }

        @Test
        void testTotalPayableCalculation() {
            ApplicantDTO applicant = createApplicant("Frank", 32, new BigDecimal("55000"), EmploymentType.SALARIED, 780);
            LoanDTO loan = createLoan(new BigDecimal("600000"), 60, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            BigDecimal emi = response.offer().emi();
            BigDecimal totalPayable = response.offer().totalPayable();
            BigDecimal expectedTotal = emi.multiply(new BigDecimal("60")).setScale(2, RoundingMode.HALF_UP);
            assertEquals(0, expectedTotal.compareTo(totalPayable), "Total payable should equal EMI * tenure");
        }
    }

    @Nested
    class EligibilityLogicTests {

        @Test
        void testEligibility_CreditScoreTooLow() {
            ApplicantDTO applicant = createApplicant("George", 30, new BigDecimal("50000"), EmploymentType.SALARIED, 550);
            LoanDTO loan = createLoan(new BigDecimal("300000"), 48, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.REJECTED, response.status());
            assertTrue(response.rejectionReasons().contains(RejectionReason.CREDIT_SCORE_TOO_LOW));
            assertNull(response.riskBand());
            assertNull(response.offer());
        }

        @Test
        void testEligibility_AgeTenureLimitExceeded() {
            ApplicantDTO applicant = createApplicant("Helen", 58, new BigDecimal("80000"), EmploymentType.SALARIED, 750);
            LoanDTO loan = createLoan(new BigDecimal("500000"), 120, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.REJECTED, response.status());
            assertTrue(response.rejectionReasons().contains(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED));
        }

        @Test
        void testEligibility_EmiExceeds60Percent() {
            ApplicantDTO applicant = createApplicant("Ivan", 30, new BigDecimal("30000"), EmploymentType.SALARIED, 750);
            LoanDTO loan = createLoan(new BigDecimal("800000"), 24, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.REJECTED, response.status());
            assertTrue(response.rejectionReasons().contains(RejectionReason.EMI_EXCEEDS_60_PERCENT));
        }

        @Test
        void testEligibility_EmiExceeds50Percent() {
            ApplicantDTO applicant = createApplicant("Julia", 30, new BigDecimal("40000"), EmploymentType.SALARIED, 750);
            LoanDTO loan = createLoan(new BigDecimal("550000"), 30, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.REJECTED, response.status());
            assertTrue(response.rejectionReasons().contains(RejectionReason.EMI_EXCEEDS_50_PERCENT));
            assertNull(response.riskBand());
            assertNull(response.offer());
        }

        @Test
        void testEligibility_ValidApplication() {
            ApplicantDTO applicant = createApplicant("Kevin", 35, new BigDecimal("70000"), EmploymentType.SALARIED, 800);
            LoanDTO loan = createLoan(new BigDecimal("500000"), 60, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.APPROVED, response.status());
            assertNotNull(response.riskBand());
            assertNotNull(response.offer());
            assertTrue(response.rejectionReasons() == null || response.rejectionReasons().isEmpty());
        }

        @Test
        void testEligibility_EmiExactly50Percent() {
            ApplicantDTO applicant = createApplicant("Laura", 30, new BigDecimal("70000"), EmploymentType.SALARIED, 800);
            LoanDTO loan = createLoan(new BigDecimal("1500000"), 60, LoanPurpose.HOME);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.APPROVED, response.status());
        }

        @Test
        void testEligibility_MultipleRejectionReasons() {
            ApplicantDTO applicant = createApplicant("Mike", 59, new BigDecimal("50000"), EmploymentType.SALARIED, 580);
            LoanDTO loan = createLoan(new BigDecimal("300000"), 120, LoanPurpose.PERSONAL);
            LoanApplicationRequest request = new LoanApplicationRequest(applicant, loan);

            when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LoanApplicationResponse response = service.processLoanApplication(request);
            assertEquals(ApplicationStatus.REJECTED, response.status());
            assertTrue(response.rejectionReasons().size() > 1);
            assertTrue(response.rejectionReasons().contains(RejectionReason.CREDIT_SCORE_TOO_LOW));
            assertTrue(response.rejectionReasons().contains(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED));
        }
    }

    private ApplicantDTO createApplicant(String name, Integer age, BigDecimal income, EmploymentType employmentType, Integer creditScore) {
        return new ApplicantDTO(name, age, income, employmentType, creditScore);
    }

    private LoanDTO createLoan(BigDecimal amount, Integer tenureMonths, LoanPurpose purpose) {
        return new LoanDTO(amount, tenureMonths, purpose);
    }
}
