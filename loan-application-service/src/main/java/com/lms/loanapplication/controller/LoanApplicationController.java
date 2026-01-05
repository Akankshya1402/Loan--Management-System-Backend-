package com.lms.loanapplication.controller;

import com.lms.loanapplication.client.CustomerClient;
import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;
import com.lms.loanapplication.service.LoanApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService service;
    private final CustomerClient customerClient; // 🔥 ADD

    @PostMapping
    public ResponseEntity<LoanApplicationResponse> apply(
            @Valid @RequestBody LoanApplicationRequest request,
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        String authUserId = authentication.getName(); // username / sub

        // 🔥 Convert auth user → internal customerId
        String customerId =
                customerClient.getCustomerIdByAuthUserId(authorizationHeader);

        LoanApplicationResponse response =
                service.apply(customerId, request, authorizationHeader);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> myLoans(
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        String customerId =
                customerClient.getCustomerIdByAuthUserId(authorizationHeader);

        return ResponseEntity.ok(
                service.getMyApplications(customerId)
        );
    }
}
