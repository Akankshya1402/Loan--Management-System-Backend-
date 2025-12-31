package com.lms.loanapplication.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "loan_applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    @Id
    private String id;

    private String customerId;
    private String loanType;
    private Double loanAmount;
    private Integer tenureMonths;
    private Double monthlyIncome;

    private LoanStatus status;
    private LocalDateTime appliedAt;
}
