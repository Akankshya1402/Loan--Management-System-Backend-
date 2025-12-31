package com.lms.customer.service;

import com.lms.customer.dto.CustomerRequest;
import com.lms.customer.exception.CustomerNotFoundException;
import com.lms.customer.model.Customer;
import com.lms.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @Test
    void shouldCreateCustomer() {
        CustomerRequest request = new CustomerRequest(
                "John",
                "john@test.com",
                50000.0
        );

        Customer saved = Customer.builder()
                .id("1")
                .name("John")
                .email("john@test.com")
                .income(50000.0)
                .kycStatus("PENDING")
                .build();

        when(repository.save(any(Customer.class))).thenReturn(saved);

        var response = service.create(request);

        assertEquals("John", response.getName());
        assertEquals("john@test.com", response.getEmail());
        assertEquals("PENDING", response.getKycStatus());
        verify(repository).save(any(Customer.class));
    }

    @Test
    void shouldReturnCustomerById() {
        Customer customer = Customer.builder()
                .id("1")
                .name("John")
                .email("john@test.com")
                .income(40000.0)
                .kycStatus("APPROVED")
                .build();

        when(repository.findById("1")).thenReturn(Optional.of(customer));

        var response = service.getById("1");

        assertEquals("John", response.getName());
        assertEquals("APPROVED", response.getKycStatus());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(repository.findById("invalid"))
                .thenReturn(Optional.empty());

        CustomerNotFoundException ex =
                assertThrows(CustomerNotFoundException.class,
                        () -> service.getById("invalid"));

        assertEquals("Customer not found with id: invalid", ex.getMessage());
    }
    @Test
    void shouldMapEntityToResponseCorrectly() {

        Customer customer = Customer.builder()
                .id("10")
                .name("Alex")
                .email("alex@test.com")
                .income(80000.0)
                .kycStatus("APPROVED")
                .build();

        when(repository.findById("10"))
                .thenReturn(Optional.of(customer));

        var response = service.getById("10");

        assertEquals("Alex", response.getName());
        assertEquals("APPROVED", response.getKycStatus());
    }

}


