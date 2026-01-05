package com.lms.payment.service.impl;

import com.lms.payment.client.CustomerClient;
import com.lms.payment.client.LoanProcessingClient;
import com.lms.payment.dto.PaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.exception.PaymentAlreadyProcessedException;
import com.lms.payment.messaging.PaymentEventProducer;
import com.lms.payment.messaging.event.PaymentFailedEvent;
import com.lms.payment.messaging.event.PaymentSuccessEvent;
import com.lms.payment.model.Payment;
import com.lms.payment.model.enums.PaymentStatus;
import com.lms.payment.repository.PaymentRepository;
import com.lms.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final LoanProcessingClient loanClient;
    private final CustomerClient customerClient;
    private final PaymentEventProducer producer;

    @Override
    public PaymentResponse makeEmiPayment(PaymentRequest request) {

        log.info("➡️ Entered makeEmiPayment");

        log.info("Request = {}", request);

        if (repository.existsByLoanIdAndEmiNumberAndStatus(
                request.getLoanId(),
                request.getEmiNumber(),
                PaymentStatus.SUCCESS)) {

            log.warn("❌ EMI already paid");
            throw new PaymentAlreadyProcessedException("EMI already paid");
        }

        log.info("✅ EMI not paid yet");

        Payment payment = Payment.builder()
                .loanId(request.getLoanId())
                .customerId(request.getCustomerId())
                .emiNumber(request.getEmiNumber())
                .amount(request.getAmount())
                .transactionRef(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .status(PaymentStatus.PENDING)
                .build();

        log.info("🟡 Saving payment (PENDING)");
        payment = repository.save(payment);

        log.info("🟢 Payment saved with id {}", payment.getPaymentId());

        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        log.info("🟢 Payment marked SUCCESS");

        loanClient.recordEmiPayment(payment.getLoanId(), payment.getEmiNumber());
        log.info("🟢 Loan processing updated");

        customerClient.updateEmiLiability(
                payment.getCustomerId(),
                payment.getAmount().negate()
        );
        log.info("🟢 Customer liability updated");

        return map(payment);
    }


    @Override
    public List<PaymentResponse> getPaymentsByLoan(String loanId) {
        return repository.findByLoanId(loanId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomer(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    private PaymentResponse map(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .loanId(payment.getLoanId())
                .emiNumber(payment.getEmiNumber())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
