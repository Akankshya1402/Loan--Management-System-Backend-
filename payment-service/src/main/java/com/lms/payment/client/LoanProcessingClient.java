package com.lms.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "loan-processing-service", path = "/api/loan-processing")
public interface LoanProcessingClient {

    @PostMapping("/loans/emi/pay")
    void recordEmiPayment(
            @RequestParam("loanId") String loanId,
            @RequestParam("emiNumber") Integer emiNumber
    );
}
