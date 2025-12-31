package com.lms.customer.exception;

import com.lms.customer.controller.CustomerController;
import com.lms.customer.exception.GlobalExceptionHandler;
import com.lms.customer.service.CustomerService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;   // 🔥 REQUIRED
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        when(customerService.getById("99"))
                .thenThrow(new CustomerNotFoundException(
                        "Customer not found with id: 99"
                ));

        mockMvc.perform(get("/api/customers/99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Customer not found with id: 99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldHandleValidationException() throws Exception {

        mockMvc.perform(post("/api/customers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

}
