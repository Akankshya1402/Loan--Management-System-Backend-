package com.lms.loanapplication.exception;

public class LoanApplicationNotFoundException
        extends RuntimeException {

    public LoanApplicationNotFoundException(String message) {
        super(message);
    }
}

