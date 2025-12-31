package com.lms.loanapplication.service.impl;

import com.lms.loanapplication.dto.LoanApplicationEvent;
import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;
import com.lms.loanapplication.exception.LoanApplicationNotFoundException;
import com.lms.loanapplication.kafka.LoanApplicationEventProducer;
import com.lms.loanapplication.model.LoanApplication;
import com.lms.loanapplication.model.LoanStatus;
import com.lms.loanapplication.repository.LoanApplicationRepository;
import com.lms.loanapplication.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final LoanApplicationEventProducer eventProducer;

    @Override
    public LoanApplicationResponse apply(LoanApplicationRequest request) {

        // =========================
        // BUSINESS VALIDATION FIRST
        // =========================
        if (request.getMonthlyIncome() < 10_000) {
            throw new IllegalArgumentException("Income too low for loan");
        }

        // =========================
        // CREATE & SAVE APPLICATION
        // =========================
        LoanApplication application = LoanApplication.builder()
                .customerId(request.getCustomerId())
                .loanType(request.getLoanType())
                .loanAmount(request.getLoanAmount())
                .tenureMonths(request.getTenureMonths())
                .monthlyIncome(request.getMonthlyIncome())
                .status(LoanStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        LoanApplication saved = repository.save(application);

        // =========================
        // PUBLISH KAFKA EVENT
        // =========================
        eventProducer.sendLoanAppliedEvent(
                LoanApplicationEvent.builder()
                        .eventType("LOAN_APPLIED")
                        .loanApplicationId(saved.getId())
                        .customerId(saved.getCustomerId())
                        .loanType(saved.getLoanType())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return mapToResponse(saved);
    }

    @Override
    public LoanApplicationResponse getById(String id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() ->
                        new LoanApplicationNotFoundException(
                                "Loan application not found with id: " + id));

        return mapToResponse(app);
    }

    @Override
    public List<LoanApplicationResponse> getByCustomer(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // MAPPER
    // =========================
    private LoanApplicationResponse mapToResponse(LoanApplication app) {
        LoanApplicationResponse res = new LoanApplicationResponse();
        res.setId(app.getId());
        res.setCustomerId(app.getCustomerId());
        res.setLoanType(app.getLoanType());
        res.setLoanAmount(app.getLoanAmount());
        res.setTenureMonths(app.getTenureMonths());
        res.setMonthlyIncome(app.getMonthlyIncome());
        res.setStatus(app.getStatus().name());
        res.setAppliedAt(app.getAppliedAt());
        return res;
    }
}

