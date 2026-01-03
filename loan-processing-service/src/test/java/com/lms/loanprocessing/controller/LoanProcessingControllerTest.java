package com.lms.loanprocessing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.loanprocessing.dto.EmiOverviewResponse;
import com.lms.loanprocessing.model.Loan;
import com.lms.loanprocessing.model.enums.LoanStatus;
import com.lms.loanprocessing.service.LoanServicingService;
import com.lms.loanprocessing.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanProcessingController.class)
class LoanProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanRepository loanRepository;

    @MockBean
    private LoanServicingService servicingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnLoanById() throws Exception {

        Loan loan = Loan.builder()
                .loanId("L1")
                .status(LoanStatus.ACTIVE)
                .emiAmount(BigDecimal.valueOf(1000))
                .build();

        when(loanRepository.findById("L1"))
                .thenReturn(Optional.of(loan));

        mockMvc.perform(get("/loans/L1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value("L1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnEmiOverview() throws Exception {

        EmiOverviewResponse response =
                EmiOverviewResponse.builder()
                        .loanId("L1")
                        .totalEmis(12)
                        .paidEmis(3)
                        .pendingEmis(9)
                        .emiAmount(BigDecimal.valueOf(1000))
                        .loanStatus(LoanStatus.ACTIVE)
                        .build();

        when(servicingService.getEmiOverview("L1"))
                .thenReturn(response);

        mockMvc.perform(get("/loans/L1/emi-overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paidEmis").value(3))
                .andExpect(jsonPath("$.pendingEmis").value(9));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldPayEmi() throws Exception {

        mockMvc.perform(post("/loans/L1/emi/1/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
