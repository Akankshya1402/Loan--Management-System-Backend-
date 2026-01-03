package com.lms.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.payment.dto.PaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.model.enums.PaymentStatus;
import com.lms.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn201OnSuccessfulPayment() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setLoanId("LN1");
        request.setCustomerId("CUST1");
        request.setEmiNumber(1);
        request.setAmount(BigDecimal.valueOf(5000));

        PaymentResponse response = PaymentResponse.builder()
                .paymentId("PAY1")
                .loanId("LN1")
                .emiNumber(1)
                .amount(BigDecimal.valueOf(5000))
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.makeEmiPayment(any(PaymentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}

