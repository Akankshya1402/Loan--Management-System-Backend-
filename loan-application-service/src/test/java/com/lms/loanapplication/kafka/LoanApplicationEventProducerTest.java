package com.lms.loanapplication.kafka;

import com.lms.loanapplication.dto.LoanApplicationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanApplicationEventProducerTest {

    @Mock
    private KafkaTemplate<String, LoanApplicationEvent> kafkaTemplate;

    @InjectMocks
    private LoanApplicationEventProducer producer;

    @BeforeEach
    void setUp() throws Exception {
        Field field = LoanApplicationEventProducer.class
                .getDeclaredField("topic");
        field.setAccessible(true);
        field.set(producer, "loan.application.events");
    }

    @Test
    void shouldSendKafkaEvent() {
        LoanApplicationEvent event = LoanApplicationEvent.builder()
                .loanApplicationId("LA1")
                .eventType("LOAN_APPLIED")
                .build();

        producer.sendLoanAppliedEvent(event);

        verify(kafkaTemplate).send(
                eq("loan.application.events"),
                eq("LA1"),
                eq(event)
        );
    }

    // ✅ ADD THIS TEST HERE (Kafka failure path)
    @Test
    void shouldNotThrowWhenKafkaFails() {
        LoanApplicationEvent event = LoanApplicationEvent.builder()
                .loanApplicationId("LA1")
                .eventType("LOAN_APPLIED")
                .build();

        doThrow(new RuntimeException("Kafka down"))
                .when(kafkaTemplate)
                .send(anyString(), anyString(), any());

        assertDoesNotThrow(() ->
                producer.sendLoanAppliedEvent(event)
        );
    }
}
