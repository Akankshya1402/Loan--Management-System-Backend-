package com.lms.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private String id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Income is mandatory")
    private Double income;

    private String kycStatus;
}
