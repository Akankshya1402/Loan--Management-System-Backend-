package com.lms.customer.service;

import com.lms.customer.dto.CustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.exception.CustomerNotFoundException;
import com.lms.customer.model.Customer;
import com.lms.customer.model.enums.AccountStatus;
import com.lms.customer.model.enums.KycStatus;
import com.lms.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    // =========================
    // CREATE CUSTOMER (JWT USER)
    // =========================
    public String getCustomerIdByAuthUserId(String authUserId) {
        return repository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"))
                .getCustomerId();
    }

    public Customer findByAuthUserId(String authUserId) {
        return repository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new IllegalStateException("Customer not found"));
    }

    public CustomerResponse create(CustomerRequest request, String authUserId) {

        repository.findByAuthUserId(authUserId).ifPresent(c -> {
            throw new IllegalStateException("Customer profile already exists");
        });

        Customer customer = Customer.builder()
                .authUserId(authUserId)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(650)
                .existingEmiLiability(BigDecimal.ZERO) // ✅ SAFE DEFAULT
                .accountStatus(AccountStatus.ACTIVE)
                .kycStatus(KycStatus.NOT_SUBMITTED)
                .emailVerified(false)
                .mobileVerified(false)
                .build();

        return mapToResponse(repository.save(customer));
    }

    // =========================
    // CUSTOMER → VIEW OWN PROFILE
    // =========================
    public CustomerResponse getMyProfile(String authUserId) {
        Customer customer = repository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer profile not found"));

        return mapToResponse(customer);
    }

    // =========================
    // INTERNAL: VERIFIED CUSTOMER CHECK
    // =========================
    public Customer getVerifiedCustomerOrThrow(String customerId) {

        Customer customer = repository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + customerId)
                );

        if (customer.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Customer account is not active");
        }

        if (customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new IllegalStateException("Customer KYC not verified");
        }

        if (!customer.isEmailVerified() || !customer.isMobileVerified()) {
            throw new IllegalStateException("Customer contact not verified");
        }

        return customer;
    }

    // =========================
    // INTERNAL: EMI LIABILITY UPDATE
    // =========================
    public void updateEmiLiability(String customerId, BigDecimal deltaAmount) {

        Customer customer = repository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"));

        BigDecimal current =
                customer.getExistingEmiLiability() != null
                        ? customer.getExistingEmiLiability()
                        : BigDecimal.ZERO;

        customer.setExistingEmiLiability(current.add(deltaAmount));
        repository.save(customer);
    }

    // =========================
    // ADMIN OPERATIONS
    // =========================
    public void updateKycStatus(String customerId, KycStatus status) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"));

        customer.setKycStatus(status);
        repository.save(customer);
    }

    public void verifyEmail(String customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"));

        customer.setEmailVerified(true);
        repository.save(customer);
    }

    public void verifyMobile(String customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"));

        customer.setMobileVerified(true);
        repository.save(customer);
    }

    // =========================
    // ADMIN: READ
    // =========================
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public CustomerResponse getById(String customerId) {

        Customer customer = repository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + customerId)
                );

        return mapToResponse(customer);
    }

    // =========================
    // ENTITY → DTO (FINAL FIX)
    // =========================
    private CustomerResponse mapToResponse(Customer customer) {

        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .mobile(customer.getMobile())
                .monthlyIncome(customer.getMonthlyIncome())
                .creditScore(customer.getCreditScore())
                .existingEmiLiability(
                        customer.getExistingEmiLiability() != null
                                ? customer.getExistingEmiLiability()
                                : BigDecimal.ZERO
                )
                .accountStatus(customer.getAccountStatus())
                .kycStatus(customer.getKycStatus())
                .build();
    }
}
