package com.lms.loanapplication.exception;

import com.lms.loanapplication.controller.LoanApplicationController;
import com.lms.loanapplication.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanApplicationController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService service;

    @Test
    void shouldHandleDomainException() throws Exception {

        when(service.apply(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()))
                .thenThrow(new KycNotVerifiedException());

        mockMvc.perform(post("/loan-applications"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("KYC not verified. Cannot apply for loan."));
    }

    @Test
    void shouldHandleGenericException() throws Exception {

        when(service.apply(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/loan-applications"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected error"));
    }
}

