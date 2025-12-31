package com.lms.loanapplication.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoanApplicationEvent {

    private String eventType;          // LOAN_APPLIED
    private String loanApplicationId;
    private String customerId;
    private String loanType;
    private LocalDateTime timestamp;
}

