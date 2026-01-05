package com.lms.loanprocessing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "loan-application-service",
        path = "/api/loans"
)
public interface LoanApplicationClient {

    @GetMapping("/{applicationId}")
    LoanApplicationResponse getApplication(
            @PathVariable String applicationId
    );

    record LoanApplicationResponse(
            String id,
            String loanType,
            String amount,
            Integer tenureMonths
    ) {}
}
