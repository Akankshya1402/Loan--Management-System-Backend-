package com.lms.loanapplication.exception;

import com.lms.loanapplication.controller.LoanApplicationController;
import com.lms.loanapplication.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;



@WebMvcTest(LoanApplicationController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService service;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnFieldErrorsOnValidationFailure() throws Exception {

        mockMvc.perform(post("/api/loan-applications")
                .with(csrf()) // ✅ required because POST + Spring Security
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) // invalid payload
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.loanAmount").exists())
                .andExpect(jsonPath("$.tenureMonths").exists())
                .andExpect(jsonPath("$.monthlyIncome").exists());
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn404WhenLoanNotFound() throws Exception {

        when(service.getById("X"))
                .thenThrow(new LoanApplicationNotFoundException(
                        "Loan application not found with id: X"
                ));

        mockMvc.perform(get("/api/loan-applications/X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Loan application not found with id: X"));
    }

}
