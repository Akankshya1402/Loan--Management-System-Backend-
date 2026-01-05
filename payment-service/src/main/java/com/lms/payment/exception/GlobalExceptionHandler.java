package com.lms.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentAlreadyProcessedException.class)
    public ResponseEntity<?> handlePaymentAlreadyProcessed(
            PaymentAlreadyProcessedException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body("Payment Success");
    }
}
