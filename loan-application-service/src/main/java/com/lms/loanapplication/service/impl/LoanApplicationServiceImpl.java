package com.lms.loanapplication.service.impl;

import com.lms.loanapplication.client.CustomerClient;
import com.lms.loanapplication.dto.CustomerProfileResponse;
import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;
import com.lms.loanapplication.kafka.LoanApplicationEventProducer;
import com.lms.loanapplication.model.LoanApplication;
import com.lms.loanapplication.model.enums.ApplicationStatus;
import com.lms.loanapplication.repository.LoanApplicationRepository;
import com.lms.loanapplication.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final CustomerClient customerClient;
    private final LoanApplicationEventProducer eventProducer;

    private static final Set<Integer> ALLOWED_TENURES =
            Set.of(12, 24, 36);

    @Override
    public LoanApplicationResponse apply(
            String customerId,
            LoanApplicationRequest request,
            String token
    ) {

        // 1️⃣ Fetch customer profile (JWT forwarded)
        CustomerProfileResponse customer =
                customerClient.getMyProfile(token);

        // 2️⃣ KYC check
        if (!"VERIFIED".equals(customer.getKycStatus())) {
            throw new IllegalStateException("KYC not verified");
        }

        // 3️⃣ Account status check
        if (!"ACTIVE".equals(customer.getAccountStatus())) {
            throw new IllegalStateException("Customer account is not active");
        }

        // 4️⃣ One active loan per type
        if (repository.existsByCustomerIdAndLoanType(
                customerId,
                request.getLoanType())) {
            throw new IllegalStateException(
                    "Active loan of this type already exists");
        }

        // 5️⃣ Tenure validation
        if (!ALLOWED_TENURES.contains(request.getTenureMonths())) {
            throw new IllegalArgumentException("Invalid tenure");
        }

        // 6️⃣ Save loan
        LoanApplication application =
                LoanApplication.builder()
                        .customerId(customerId)
                        .loanType(request.getLoanType())
                        .loanAmount(request.getLoanAmount())
                        .tenureMonths(request.getTenureMonths())
                        .status(ApplicationStatus.SUBMITTED)
                        .createdAt(LocalDateTime.now())
                        .build();

        LoanApplication saved = repository.save(application);

        // 7️⃣ Publish event
        eventProducer.publishSubmitted(saved);

        return map(saved);
    }

    @Override
    public List<LoanApplicationResponse> getMyApplications(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    private LoanApplicationResponse map(LoanApplication app) {
        LoanApplicationResponse res = new LoanApplicationResponse();
        res.setApplicationId(app.getApplicationId());
        res.setCustomerId(app.getCustomerId());
        res.setLoanType(app.getLoanType());
        res.setLoanAmount(app.getLoanAmount());
        res.setTenureMonths(app.getTenureMonths());
        res.setStatus(app.getStatus());
        res.setAppliedAt(app.getCreatedAt());
        return res;
    }
}
