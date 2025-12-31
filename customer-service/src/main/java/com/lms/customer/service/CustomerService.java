package com.lms.customer.service;

import com.lms.customer.dto.CustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.exception.CustomerNotFoundException;
import com.lms.customer.model.Customer;
import com.lms.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    // =========================
    // CREATE CUSTOMER
    // =========================
    public CustomerResponse create(CustomerRequest request) {

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .income(request.getIncome())
                .kycStatus("PENDING")
                .build();

        Customer savedCustomer = repository.save(customer);
        return mapToResponse(savedCustomer);
    }

    // =========================
    // GET ALL CUSTOMERS (ADMIN)
    // =========================
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // =========================
    // GET CUSTOMER BY ID (ADMIN)
    // =========================
    public CustomerResponse getById(String id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found with id: " + id)
                );

        return mapToResponse(customer);
    }

    // =========================
    // ENTITY → DTO MAPPER
    // =========================
    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .income(customer.getIncome())
                .kycStatus(customer.getKycStatus())
                .build();
    }
}



