package com.lms.loanapplication.service;

import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;

import java.util.List;

public interface LoanApplicationService {

    LoanApplicationResponse apply(LoanApplicationRequest request);

    LoanApplicationResponse getById(String id);

    List<LoanApplicationResponse> getByCustomer(String customerId);
}

