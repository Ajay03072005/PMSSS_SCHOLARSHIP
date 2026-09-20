package com.pmsss.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private long totalApplications;
    private long approvedApplications;
    private long rejectedApplications;
    private long pendingApplications;
    private long totalUsers;
    private long totalStudents;
    private BigDecimal totalDisbursedAmount;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> stateDistribution;
    private Map<String, Long> monthlyTrend;
}
