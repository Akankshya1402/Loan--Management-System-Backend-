package com.lms.loanapplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoanApplicationResponse {

    private String id;
    private String customerId;
    private String loanType;
    private Double loanAmount;
    private Integer tenureMonths;
    private Double monthlyIncome;
    private String status;
    private LocalDateTime appliedAt;
}
