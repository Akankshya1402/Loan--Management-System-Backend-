package com.lms.loanapplication.service;

import com.lms.loanapplication.dto.LoanApplicationRequest;
import com.lms.loanapplication.dto.LoanApplicationResponse;

import java.util.List;

public interface LoanApplicationService {

    LoanApplicationResponse apply(
            String customerId,
            LoanApplicationRequest request,
            String token
    );

    List<LoanApplicationResponse> getMyApplications(String customerId);
}
