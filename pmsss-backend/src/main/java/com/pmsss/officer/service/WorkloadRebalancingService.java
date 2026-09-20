package com.pmsss.officer.service;

import com.pmsss.audit.service.AuditLogService;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.officer.dto.RebalanceSummaryDto;
import com.pmsss.officer.entity.ApplicationAssignment;
import com.pmsss.officer.entity.OfficerWorkload;
import com.pmsss.officer.repository.ApplicationAssignmentRepository;
import com.pmsss.officer.repository.OfficerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadRebalancingService {

    private final OfficerWorkloadRepository officerWorkloadRepository;
    private final ApplicationAssignmentRepository assignmentRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public RebalanceSummaryDto rebalanceWorkloads(String adminUsername) {
        log.info("Executing workload rebalancing requested by {}", adminUsername);

        List<OfficerWorkload> overloaded = officerWorkloadRepository.findOverloadedOfficers();
        List<OfficerWorkload> available = officerWorkloadRepository.findAvailableOfficersByUtilization();

        int reassignmentsCount = 0;
        List<String> details = new ArrayList<>();

        if (overloaded.isEmpty()) {
            return RebalanceSummaryDto.builder()
                    .overloadedOfficersCount(0)
                    .availableOfficersCount(available.size())
                    .reassignmentsCount(0)
                    .details(List.of("No officers currently exceed maximum workload capacity."))
                    .build();
        }

        for (OfficerWorkload source : overloaded) {
            int excess = source.getCurrentWorkload() - source.getMaxCapacity() + 5; // Rebalance to slight breathing room
            if (excess <= 0) continue;

            List<ApplicationAssignment> activeAssignments = assignmentRepository.findActiveWorkQueueForOfficer(source.getOfficer().getId());

            int toMove = Math.min(excess, activeAssignments.size());
            for (int i = 0; i < toMove; i++) {
                if (available.isEmpty()) {
                    log.warn("Cannot reassign remaining applications: no officers available with capacity");
                    break;
                }

                // Pick destination officer with lowest current load
                OfficerWorkload dest = available.get(0);
                ApplicationAssignment assignment = activeAssignments.get(i);

                // Mark previous assignment reassigned
                assignment.setStatus("REASSIGNED");
                assignment.setCompletedAt(LocalDateTime.now());
                assignmentRepository.save(assignment);

                // Create new assignment for destination officer
                ApplicationAssignment newAssignment = ApplicationAssignment.builder()
                        .applicationId(assignment.getApplicationId())
                        .officer(dest.getOfficer())
                        .officerName(dest.getOfficerName())
                        .officerEmail(dest.getOfficerEmail())
                        .assignedBy("SYSTEM_LOAD_REBALANCE")
                        .priority(assignment.getPriority())
                        .reviewCategory(assignment.getReviewCategory())
                        .status("ACTIVE")
                        .slaDueAt(assignment.getSlaDueAt())
                        .triageReason("Workload rebalance transfer from " + source.getOfficerName())
                        .build();
                assignmentRepository.save(newAssignment);

                // Update counters
                source.setCurrentWorkload(Math.max(0, source.getCurrentWorkload() - 1));
                dest.setCurrentWorkload(dest.getCurrentWorkload() + 1);
                reassignmentsCount++;

                String detailMsg = String.format("Reassigned app %s from %s to %s",
                        assignment.getApplicationId(), source.getOfficerName(), dest.getOfficerName());
                details.add(detailMsg);

                // Audit log
                auditLogService.logAction(
                        adminUsername != null ? adminUsername : "SYSTEM",
                        "ADMIN",
                        "WORKLOAD_REBALANCED",
                        "ApplicationAssignment",
                        assignment.getApplicationId(),
                        source.getOfficerEmail(),
                        dest.getOfficerEmail()
                );

                // Notify destination officer
                notificationService.createNotification(
                        dest.getOfficer(),
                        null,
                        "REBALANCED_ASSIGNMENT",
                        "Rebalanced Assignment: " + assignment.getApplicationId(),
                        "Transferred to balance officer workload. Priority: " + assignment.getPriority()
                );

                // Re-sort available officers if dest is full
                if (dest.getCurrentWorkload() >= dest.getMaxCapacity()) {
                    available.remove(dest);
                }
            }

            officerWorkloadRepository.save(source);
        }

        // Save updated available workloads
        officerWorkloadRepository.saveAll(available);

        log.info("Workload rebalancing completed: {} reassignments across {} overloaded officers",
                reassignmentsCount, overloaded.size());

        return RebalanceSummaryDto.builder()
                .overloadedOfficersCount(overloaded.size())
                .availableOfficersCount(available.size())
                .reassignmentsCount(reassignmentsCount)
                .details(details)
                .build();
    }
}
