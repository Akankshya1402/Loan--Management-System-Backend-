package com.lms.loanapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;
import com.lms.loanapplication.exception.GlobalExceptionHandler;
import com.lms.loanapplication.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(LoanApplicationController.class)
@Import(GlobalExceptionHandler.class)
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(roles = "CUSTOMER")   // ✅ AUTH
    void shouldCreateLoanApplication() throws Exception {

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setStatus("APPLIED");

        when(service.apply(any())).thenReturn(response);

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setCustomerId("C1");
        request.setLoanType("HOME");
        request.setLoanAmount(500000.0);
        request.setTenureMonths(120);
        request.setMonthlyIncome(50000.0);

        mockMvc.perform(post("/api/loan-applications")
                .with(csrf()) // ✅ CSRF
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")   // ✅ AUTH
    void shouldReturn400WhenValidationFails() throws Exception {

        mockMvc.perform(post("/api/loan-applications")
                .with(csrf()) // ✅ CSRF
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/loan-applications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn403WhenRoleIsNotCustomer() throws Exception {
        mockMvc.perform(post("/api/loan-applications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldGetLoanById() throws Exception {

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setId("LA1");
        response.setStatus("APPLIED");

        when(service.getById("LA1")).thenReturn(response);

        mockMvc.perform(get("/api/loan-applications/LA1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("LA1"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldGetLoansByCustomer() throws Exception {

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setId("LA1");

        when(service.getByCustomer("C1"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/loan-applications/customer/C1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("LA1"));
    }




}
