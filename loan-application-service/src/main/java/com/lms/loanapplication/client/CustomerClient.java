package com.lms.loanapplication.client;

import com.lms.loanapplication.dto.CustomerProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/me")
    CustomerProfileResponse getMyProfile(
            @RequestHeader("Authorization") String token
    );
    @GetMapping("/api/customers/me/id")
    String getCustomerIdByAuthUserId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    );
}
