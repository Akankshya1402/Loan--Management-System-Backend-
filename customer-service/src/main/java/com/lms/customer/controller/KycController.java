package com.lms.customer.controller;

import com.lms.customer.model.KycDocument;
import com.lms.customer.service.CustomerService;
import com.lms.customer.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KycController {

    private final KycService kycService;
    private final CustomerService customerService;

    // =========================
    // CUSTOMER → UPLOAD KYC DOCUMENT
    // =========================
    @PostMapping("/customers/me/kyc")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> uploadKyc(
            @RequestBody KycDocument document,
            Authentication authentication) {

        // 🔐 authUserId from JWT (sub)
        String authUserId = authentication.getName();

        // 🔥 Resolve internal customerId
        String customerId =
                customerService.getCustomerIdByAuthUserId(authUserId);

        // Server-controlled fields
        document.setCustomerId(customerId);

        kycService.uploadDocument(document);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // =========================
    // CUSTOMER → VIEW OWN KYC DOCUMENTS
    // =========================
    @GetMapping("/customers/me/kyc")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<KycDocument>> getMyKycDocuments(
            Authentication authentication) {

        String authUserId = authentication.getName();

        String customerId =
                customerService.getCustomerIdByAuthUserId(authUserId);

        return ResponseEntity.ok(
                kycService.getMyDocuments(customerId)
        );
    }

    // =========================
    // ADMIN → APPROVE KYC DOCUMENT
    // =========================
    @PutMapping("/admin/kyc/{documentId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveKyc(
            @PathVariable String documentId,
            @RequestParam(required = false) String remarks) {

        kycService.approveDocument(documentId, remarks);
        return ResponseEntity.ok().build();
    }

    // =========================
    // ADMIN → REJECT KYC DOCUMENT
    // =========================
    @PutMapping("/admin/kyc/{documentId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectKyc(
            @PathVariable String documentId,
            @RequestParam String remarks) {

        kycService.rejectDocument(documentId, remarks);
        return ResponseEntity.ok().build();
    }
    // =========================
// ADMIN → VIEW ALL KYC DOCUMENTS
// =========================
    @GetMapping("/admin/kyc")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KycDocument>> getAllKycDocuments() {
        return ResponseEntity.ok(kycService.getAllDocuments());
    }
    @GetMapping("/admin/test")
    public String adminTest() {
        return "ADMIN CONTROLLER WORKING";
    }


}
