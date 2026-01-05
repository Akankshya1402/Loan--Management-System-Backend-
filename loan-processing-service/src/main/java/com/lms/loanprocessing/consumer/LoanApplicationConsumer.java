package com.lms.loanprocessing.consumer;

import com.lms.loanprocessing.event.LoanApplicationSubmittedEvent;
import com.lms.loanprocessing.service.LoanApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanApplicationConsumer {

    private final LoanApprovalService approvalService;

    @KafkaListener(
            topics = "loan-application-submitted",
            containerFactory = "loanApplicationListenerFactory"
    )
    public void consume(LoanApplicationSubmittedEvent event) {
        try {
            log.info("📥 Processing application {}", event.getApplicationId());
            approvalService.process(event);
        } catch (Exception ex) {
            log.error("❌ Failed processing application {}", event.getApplicationId(), ex);
            // DO NOT rethrow
        }
    }

}
