package com.lms.loanapplication.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerProfileResponse {

    private String customerId;
    private BigDecimal monthlyIncome;
    private Integer creditScore;

    // 👇 USE STRING (JSON-friendly)
    private String accountStatus; // ACTIVE, BLOCKED
    private String kycStatus;     // VERIFIED, PENDING
}
