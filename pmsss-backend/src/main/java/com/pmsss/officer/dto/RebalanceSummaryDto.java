package com.pmsss.officer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RebalanceSummaryDto {
    private int overloadedOfficersCount;
    private int availableOfficersCount;
    private int reassignmentsCount;
    private List<String> details;
}
