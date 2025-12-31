package com.lms.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.customer.dto.CustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================
    // CREATE CUSTOMER (ADMIN)
    // =========================
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldCreateCustomer() throws Exception {

        CustomerRequest request = new CustomerRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setIncome(50000.0);

        CustomerResponse response = new CustomerResponse();
        response.setId("1");
        response.setName("John");
        response.setEmail("john@test.com");
        response.setIncome(50000.0);
        response.setKycStatus("PENDING");

        when(service.create(any(CustomerRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/customers")
                .with(csrf()) // 🔥 REQUIRED
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.kycStatus").value("PENDING"));
    }

    // =========================
    // GET CUSTOMER BY ID (ADMIN)
    // =========================
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldGetCustomerById() throws Exception {

        CustomerResponse response = new CustomerResponse();
        response.setId("1");
        response.setName("Bob");
        response.setEmail("bob@test.com");
        response.setIncome(70000.0);
        response.setKycStatus("APPROVED");

        when(service.getById("1")).thenReturn(response);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("bob@test.com"))
                .andExpect(jsonPath("$.kycStatus").value("APPROVED"));
    }

    // =========================
    // SECURITY: NOT AUTHENTICATED
    // =========================
    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void shouldReturn400WhenValidationFailsEvenIfRoleIsWrong() throws Exception {

        mockMvc.perform(post("/api/customers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }


}


