package com.pmsss.dashboard.model;

import com.pmsss.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceDashboardDto {
    private long approvedApplicationsReadyForFinance;
    private long paymentPendingCount;
    private long paymentProcessingCount;
    private long paymentCompletedCount;
    private long paymentFailedCount;
    private BigDecimal totalDisbursedAmount;
    private List<Payment> recentPayments;
}
