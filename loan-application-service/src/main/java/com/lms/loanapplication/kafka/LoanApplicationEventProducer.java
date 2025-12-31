package com.lms.loanapplication.kafka;

import com.lms.loanapplication.dto.LoanApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanApplicationEventProducer {

    @Value("${kafka.topics.loan-application}")
    private String topic;

    private final KafkaTemplate<String, LoanApplicationEvent> kafkaTemplate;

    public void sendLoanAppliedEvent(LoanApplicationEvent event) {
        try {
            kafkaTemplate.send(topic, event.getLoanApplicationId(), event);
            log.info("Loan application event published. id={}", event.getLoanApplicationId());
        } catch (Exception ex) {
            // DO NOT break business flow if Kafka is down
            log.error("Failed to publish loan application event. id={}", 
                      event.getLoanApplicationId(), ex);
        }
    }
}


