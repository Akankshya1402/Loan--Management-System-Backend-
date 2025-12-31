package com.lms.customer.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String id;
    private String name;
    private String email;
    private Double income;
    private String kycStatus;
}
