package com.lms.loanapplication.service;

import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.exception.LoanApplicationNotFoundException;
import com.lms.loanapplication.kafka.LoanApplicationEventProducer;
import com.lms.loanapplication.model.LoanApplication;
import com.lms.loanapplication.model.LoanStatus;
import com.lms.loanapplication.repository.LoanApplicationRepository;
import com.lms.loanapplication.service.impl.LoanApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private LoanApplicationEventProducer eventProducer;

    @InjectMocks
    private LoanApplicationServiceImpl service;

    @Test
    void shouldApplyLoanAndPublishEvent() {

        LoanApplication saved = LoanApplication.builder()
                .id("LA1")
                .status(LoanStatus.APPLIED)
                .build();

        when(repository.save(any())).thenReturn(saved);

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setCustomerId("C1");
        request.setLoanType("HOME");
        request.setLoanAmount(500000.0);
        request.setTenureMonths(120);
        request.setMonthlyIncome(50000.0);

        var response = service.apply(request);

        assertEquals("APPLIED", response.getStatus());
        verify(repository).save(any());
        verify(eventProducer).sendLoanAppliedEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenLoanNotFound() {
        when(repository.findById("X")).thenReturn(Optional.empty());

        assertThrows(
                LoanApplicationNotFoundException.class,
                () -> service.getById("X")
        );
    }
    @Test
    void shouldRejectLoanWhenIncomeTooLow() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setCustomerId("C1");
        request.setLoanType("HOME");
        request.setLoanAmount(500000.0);
        request.setTenureMonths(120);
        request.setMonthlyIncome(5000.0); // too low

        assertThrows(
                IllegalArgumentException.class,
                () -> service.apply(request)
        );

        verify(repository, never()).save(any());
        verify(eventProducer, never()).sendLoanAppliedEvent(any());
    }
    @Test
    void shouldReturnLoanById() {
        LoanApplication loan = LoanApplication.builder()
                .id("LA1")
                .status(LoanStatus.APPLIED)
                .build();

        when(repository.findById("LA1")).thenReturn(Optional.of(loan));

        var response = service.getById("LA1");

        assertEquals("APPLIED", response.getStatus());
    }


}
