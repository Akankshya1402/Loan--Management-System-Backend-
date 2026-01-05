package com.lms.loanprocessing.controller;

import com.lms.loanprocessing.dto.EmiOverviewResponse;
import com.lms.loanprocessing.exception.LoanNotFoundException;
import com.lms.loanprocessing.model.EmiSchedule;
import com.lms.loanprocessing.model.Loan;
import com.lms.loanprocessing.model.enums.LoanStatus;
import com.lms.loanprocessing.repository.EmiScheduleRepository;
import com.lms.loanprocessing.repository.LoanRepository;
import com.lms.loanprocessing.service.LoanServicingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.lms.loanprocessing.client.CustomerClient;

import java.util.List;

@RestController
@RequestMapping("/api/loan-processing")
@RequiredArgsConstructor
public class LoanProcessingController {

    private final LoanRepository loanRepository;
    private final LoanServicingService servicingService;
    private final CustomerClient customerClient;
    private final EmiScheduleRepository emiRepository;

    // =========================
    // GET LOAN DETAILS
    // =========================
    @GetMapping("/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<Loan> getLoan(@PathVariable String loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new LoanNotFoundException("Loan not found"));

        return ResponseEntity.ok(loan);
    }

    // =========================
    // EMI OVERVIEW
    // =========================
    @GetMapping("/{loanId}/emi-overview")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<EmiOverviewResponse> getEmiOverview(
            @PathVariable String loanId
    ) {
        return ResponseEntity.ok(
                servicingService.getEmiOverview(loanId)
        );
    }

    // =========================
    // EMI SCHEDULE
    // =========================
    @GetMapping("/{loanId}/emis")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<EmiSchedule>> getEmisByLoan(
            @PathVariable String loanId
    ) {
        return ResponseEntity.ok(
                emiRepository.findByLoanId(loanId)
        );
    }

    // =========================
    // MY ACTIVE LOANS
    // =========================
    @GetMapping("/my/active")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Loan>> myActiveLoans(
            @RequestHeader("Authorization") String token
    ) {
        String customerId = customerClient.getMyCustomerId(token);

        return ResponseEntity.ok(
                loanRepository.findByCustomerIdAndStatus(
                        customerId,
                        LoanStatus.ACTIVE
                )
        );
    }

    // =========================
    // INTERNAL → RECORD EMI PAYMENT (PAYMENT SERVICE)
    // OPTION 1: permitAll (DEV MODE)
    // =========================
    @PostMapping("/loans/emi/pay")
    @PreAuthorize("permitAll()")
    public void recordEmiPayment(
            @RequestParam String loanId,
            @RequestParam Integer emiNumber
    ) {
        servicingService.markEmiPaid(loanId, emiNumber);
    }
}
