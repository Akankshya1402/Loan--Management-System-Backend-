package com.lms.analytics.service.impl;

import com.lms.analytics.dto.DashboardResponse;
import com.lms.analytics.model.CustomerAnalytics;
import com.lms.analytics.model.LoanAnalytics;
import com.lms.analytics.model.PaymentAnalytics;
import com.lms.analytics.repository.CustomerAnalyticsRepository;
import com.lms.analytics.repository.LoanAnalyticsRepository;
import com.lms.analytics.repository.PaymentAnalyticsRepository;
import com.lms.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final LoanAnalyticsRepository loanAnalyticsRepository;
    private final CustomerAnalyticsRepository customerAnalyticsRepository;
    private final PaymentAnalyticsRepository paymentAnalyticsRepository;

    @Override
    public DashboardResponse getDashboard() {

        List<LoanAnalytics> loanStats = loanAnalyticsRepository.findAll();

        long totalLoans = loanStats.stream()
                .mapToLong(LoanAnalytics::getTotalApplications)
                .sum();

        long approved = loanStats.stream()
                .mapToLong(LoanAnalytics::getApprovedCount)
                .sum();

        long pending = loanStats.stream()
                .mapToLong(LoanAnalytics::getPendingCount)
                .sum();

        long rejected = loanStats.stream()
                .mapToLong(LoanAnalytics::getRejectedCount)
                .sum();

        BigDecimal totalDisbursed = loanStats.stream()
                .map(LoanAnalytics::getTotalDisbursedAmount)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomerAnalytics customerAnalytics =
                customerAnalyticsRepository
                        .findById("GLOBAL")
                        .orElse(CustomerAnalytics.builder()
                                .activeCustomers(0)
                                .build());

        PaymentAnalytics paymentAnalytics =
                paymentAnalyticsRepository
                        .findById("GLOBAL")
                        .orElse(PaymentAnalytics.builder()
                                .totalCollected(BigDecimal.ZERO)
                                .build());

        return DashboardResponse.builder()
                .totalLoans(totalLoans)
                .approvedLoans(approved)
                .pendingLoans(pending)
                .rejectedLoans(rejected)
                .totalDisbursedAmount(totalDisbursed)
                .activeCustomers(customerAnalytics.getActiveCustomers())
                .totalEmiCollected(paymentAnalytics.getTotalCollected())
                .build();
    }
}

