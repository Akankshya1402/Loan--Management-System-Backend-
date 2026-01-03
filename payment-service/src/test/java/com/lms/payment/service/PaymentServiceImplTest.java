package com.lms.payment.service;

import com.lms.payment.client.CustomerClient;
import com.lms.payment.client.LoanProcessingClient;
import com.lms.payment.dto.PaymentRequest;
import com.lms.payment.exception.PaymentAlreadyProcessedException;
import com.lms.payment.messaging.PaymentEventProducer;
import com.lms.payment.model.Payment;
import com.lms.payment.model.enums.PaymentStatus;
import com.lms.payment.repository.PaymentRepository;
import com.lms.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private LoanProcessingClient loanClient;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private PaymentEventProducer producer;

    @InjectMocks
    private PaymentServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMakePaymentSuccessfully() {

        PaymentRequest request = new PaymentRequest();
        request.setLoanId("LN1");
        request.setCustomerId("CUST1");
        request.setEmiNumber(1);
        request.setAmount(BigDecimal.valueOf(5000));

        when(repository.existsByLoanIdAndEmiNumberAndStatus(
                "LN1", 1, PaymentStatus.SUCCESS))
                .thenReturn(false);

        when(repository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.makeEmiPayment(request);

        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(loanClient).recordEmiPayment("LN1", 1);
        verify(customerClient).reduceEmiLiability("CUST1", BigDecimal.valueOf(5000));
        verify(producer).publishPaymentSuccess(any());
    }

    @Test
    void shouldThrowExceptionIfEmiAlreadyPaid() {

        PaymentRequest request = new PaymentRequest();
        request.setLoanId("LN1");
        request.setCustomerId("CUST1");
        request.setEmiNumber(1);
        request.setAmount(BigDecimal.TEN);

        when(repository.existsByLoanIdAndEmiNumberAndStatus(
                "LN1", 1, PaymentStatus.SUCCESS))
                .thenReturn(true);

        assertThrows(PaymentAlreadyProcessedException.class,
                () -> service.makeEmiPayment(request));

        verifyNoInteractions(loanClient, customerClient, producer);
    }

    @Test
    void shouldFetchPaymentsByLoan() {

        when(repository.findByLoanId("LN1"))
                .thenReturn(List.of(Payment.builder()
                        .loanId("LN1")
                        .status(PaymentStatus.SUCCESS)
                        .build()));

        assertEquals(1, service.getPaymentsByLoan("LN1").size());
    }
}
