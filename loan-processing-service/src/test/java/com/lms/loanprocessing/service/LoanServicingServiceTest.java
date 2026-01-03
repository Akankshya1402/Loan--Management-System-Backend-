package com.lms.loanprocessing.service;

import com.lms.loanprocessing.dto.EmiOverviewResponse;
import com.lms.loanprocessing.model.EmiSchedule;
import com.lms.loanprocessing.model.Loan;
import com.lms.loanprocessing.model.enums.EmiStatus;
import com.lms.loanprocessing.model.enums.LoanStatus;
import com.lms.loanprocessing.repository.EmiScheduleRepository;
import com.lms.loanprocessing.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LoanServicingServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private EmiScheduleRepository emiRepository;

    @InjectMocks
    private LoanServicingService service;

    @Test
    void shouldMarkEmiPaid() {

        EmiSchedule emi =
                EmiSchedule.builder()
                        .loanId("L1")
                        .emiNumber(1)
                        .status(EmiStatus.PENDING)
                        .build();

        Loan loan =
                Loan.builder()
                        .loanId("L1")
                        .emiAmount(BigDecimal.valueOf(1000))
                        .outstandingAmount(BigDecimal.valueOf(1000))
                        .status(LoanStatus.ACTIVE)
                        .build();

        when(emiRepository.findByLoanId("L1")).thenReturn(List.of(emi));
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));

        service.markEmiPaid("L1", 1);

        assertEquals(EmiStatus.PAID, emi.getStatus());
        verify(loanRepository).save(any());
    }

    @Test
    void shouldReturnCorrectEmiOverview() {

        Loan loan =
                Loan.builder()
                        .loanId("L2")
                        .emiAmount(BigDecimal.valueOf(2000))
                        .outstandingAmount(BigDecimal.valueOf(8000))
                        .status(LoanStatus.ACTIVE)
                        .build();

        when(loanRepository.findById("L2"))
                .thenReturn(Optional.of(loan));

        when(emiRepository.findByLoanId("L2"))
                .thenReturn(List.of(
                        EmiSchedule.builder().status(EmiStatus.PAID).build(),
                        EmiSchedule.builder().status(EmiStatus.PENDING).build()
                ));

        EmiOverviewResponse overview =
                service.getEmiOverview("L2");

        assertEquals(1, overview.getPaidEmis());
        assertEquals(1, overview.getPendingEmis());
    }
}
