package com.lms.loanapplication.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanApplicationRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Loan type is required")
    private String loanType;

    @NotNull
    @Positive(message = "Loan amount must be positive")
    private Double loanAmount;

    @NotNull
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenureMonths;

    @NotNull
    @Positive(message = "Monthly income must be positive")
    private Double monthlyIncome;
}
