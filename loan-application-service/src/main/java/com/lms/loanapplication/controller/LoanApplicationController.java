package com.lms.loanapplication.controller;

import com.lms.loanapplication.dto.*;
import com.lms.loanapplication.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplicationResponse apply(
            @Valid @RequestBody LoanApplicationRequest request) {
        return service.apply(request);
    }

    @GetMapping("/{id}")
    public LoanApplicationResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<LoanApplicationResponse> getByCustomer(
            @PathVariable String customerId) {
        return service.getByCustomer(customerId);
    }
}
