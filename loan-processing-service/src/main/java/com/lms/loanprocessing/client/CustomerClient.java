package com.lms.loanprocessing.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(
        name = "customer-service",
        path = "/api/customers"
)
public interface CustomerClient {

    // =========================
    // FETCH CUSTOMER PROFILE
    // =========================
    @GetMapping("/{customerId}/profile")
    CustomerProfile getProfile(
            @PathVariable String customerId,
            @RequestHeader("Authorization") String token
    );

    // =========================
    // UPDATE EMI LIABILITY
    // =========================
    @PutMapping("/{customerId}/emi-liability")
    void updateEmiLiability(
            @PathVariable String customerId,
            @RequestParam BigDecimal emiAmount,
            @RequestHeader("Authorization") String token
    );
    @GetMapping("/me/id")
    String getMyCustomerId(
            @RequestHeader("Authorization") String token
    );

    // =========================
    // DTO
    // =========================
    @Data
    class CustomerProfile {
        private String customerId;
        private String email;
        private Integer creditScore;
        private BigDecimal monthlyIncome;
        private BigDecimal existingEmiLiability;
    }
}
