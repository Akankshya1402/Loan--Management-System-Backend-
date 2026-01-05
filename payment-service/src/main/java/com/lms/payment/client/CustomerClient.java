package com.lms.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;

@FeignClient(name = "customer-service",path = "/api/customers")
public interface CustomerClient {

    @PutMapping("/{customerId}/emi-liability")
    void updateEmiLiability(
            @PathVariable String customerId,
            @RequestParam BigDecimal emiAmount
    );
}

