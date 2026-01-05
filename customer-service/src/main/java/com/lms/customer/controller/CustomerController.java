package com.lms.customer.controller;

import com.lms.customer.dto.CustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.lms.customer.model.Customer;
import com.lms.customer.model.enums.AccountStatus;
import com.lms.customer.model.enums.KycStatus;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerService service;

    // =========================
    // CREATE CUSTOMER PROFILE
    // =========================
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CustomerRequest request,
            Authentication authentication
    ) {
        String authUserId = authentication.getName();
        CustomerResponse response = service.create(request, authUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/me/id")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> getMyCustomerId(Authentication authentication) {

        String authUserId = authentication.getName(); // JWT sub
        String customerId = service.getCustomerIdByAuthUserId(authUserId);

        return ResponseEntity.ok(customerId);
    }

    // =========================
    // CUSTOMER → VIEW OWN PROFILE
    // =========================
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerResponse> getMyProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                service.getMyProfile(authentication.getName())
        );
    }

    // =========================
    // ADMIN → VIEW ALL CUSTOMERS
    // =========================
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<CustomerResponse>> getAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // =========================
    // ADMIN → VIEW CUSTOMER BY ID
    // =========================
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomerResponse> getById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }
    @GetMapping("/validate-loan")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public void validateCustomerForLoan(Authentication authentication) {

        String authUserId = authentication.getName();

        Customer customer = service.findByAuthUserId(authUserId);

        if (customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new IllegalStateException("KYC not verified");
        }

        if (customer.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account not active");
        }
    }
    // =========================
// INTERNAL → FETCH CUSTOMER PROFILE (FOR LOAN PROCESSING)
// =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{customerId}/profile")
    public ResponseEntity<CustomerResponse> getProfileByCustomerId(
            @PathVariable String customerId
    ) {
        return ResponseEntity.ok(service.getById(customerId));
    }

    // =========================
// INTERNAL → UPDATE EMI LIABILITY
// =========================
    @PutMapping("/{customerId}/emi-liability")
    @PreAuthorize("permitAll()")   // 👈 ADD THIS
    public void updateEmiLiability(
            @PathVariable String customerId,
            @RequestParam BigDecimal emiAmount
    ) {
        service.updateEmiLiability(customerId, emiAmount);
    }




}
