
import com.lms.loanapplication.client.CustomerClient;
import com.lms.loanapplication.dto.CustomerProfileResponse;
import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;
import com.lms.loanapplication.kafka.LoanApplicationEventProducer;
import com.lms.loanapplication.model.LoanApplication;
import com.lms.loanapplication.model.enums.ApplicationStatus;
import com.lms.loanapplication.model.enums.LoanType;
import com.lms.loanapplication.repository.LoanApplicationRepository;
import com.lms.loanapplication.service.impl.LoanApplicationServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private LoanApplicationEventProducer eventProducer;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private LoanApplicationServiceImpl service;

    @Test
    void shouldApplyForLoanAndPublishEvent() {

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanType(LoanType.PERSONAL);
        request.setLoanAmount(BigDecimal.valueOf(200000));
        request.setTenureMonths(24);

        CustomerProfileResponse customer = new CustomerProfileResponse();
        customer.setCustomerId("CUST1");
        customer.setKycStatus("VERIFIED");
        customer.setAccountStatus("ACTIVE");
        customer.setMonthlyIncome(BigDecimal.valueOf(50000));
        customer.setCreditScore(750);

        when(customerClient.getMyProfile(any()))
                .thenReturn(customer);

        when(repository.existsByCustomerIdAndLoanType(any(), any()))
                .thenReturn(false);

        LoanApplication saved = LoanApplication.builder()
                .applicationId("APP1")
                .customerId("CUST1")
                .loanType(LoanType.PERSONAL)
                .loanAmount(request.getLoanAmount())
                .tenureMonths(24)
                .status(ApplicationStatus.SUBMITTED)
                .build();

        when(repository.save(any()))
                .thenReturn(saved);

        LoanApplicationResponse response =
                service.apply("CUST1", request, "Bearer test-token");

        assertEquals("APP1", response.getApplicationId());
        assertEquals(ApplicationStatus.SUBMITTED, response.getStatus());

        verify(customerClient).getMyProfile(any());
        verify(repository).save(any());
        verify(eventProducer).publishSubmitted(any());
    }

    @Test
    void shouldReturnCustomerApplications() {

        when(repository.findByCustomerId("C1"))
                .thenReturn(List.of(
                        LoanApplication.builder()
                                .applicationId("APP1")
                                .status(ApplicationStatus.SUBMITTED)
                                .build()
                ));

        List<LoanApplicationResponse> responses =
                service.getMyApplications("C1");

        assertEquals(1, responses.size());
        verify(repository).findByCustomerId("C1");
    }
}
