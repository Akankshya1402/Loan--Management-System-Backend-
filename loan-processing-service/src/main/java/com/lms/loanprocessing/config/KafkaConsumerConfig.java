package com.lms.loanprocessing.config;

import com.lms.loanprocessing.event.LoanApplicationSubmittedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.*;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, LoanApplicationSubmittedEvent> consumerFactory() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "loan-processing-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 🔴 IMPORTANT
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.lms.loanprocessing.event.LoanApplicationSubmittedEvent");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(LoanApplicationSubmittedEvent.class, false)
        );
    }



    // 🔹 FOR LoanApplicationSubmittedEvent
    @Bean(name = "loanApplicationListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LoanApplicationSubmittedEvent>
    loanApplicationListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, LoanApplicationSubmittedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // 🔹 FOR LoanApprovedEvent (can reuse same consumerFactory if JSON compatible)
    @Bean(name = "loanApprovedKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LoanApplicationSubmittedEvent>
    loanApprovedKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, LoanApplicationSubmittedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
