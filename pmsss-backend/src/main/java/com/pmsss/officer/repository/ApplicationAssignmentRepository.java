package com.pmsss.officer.repository;

import com.pmsss.officer.entity.ApplicationAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationAssignmentRepository extends JpaRepository<ApplicationAssignment, Long> {

    Optional<ApplicationAssignment> findByApplicationIdAndStatus(String applicationId, String status);

    List<ApplicationAssignment> findByOfficerIdAndStatus(Long officerId, String status);

    Page<ApplicationAssignment> findByOfficerIdAndStatus(Long officerId, String status, Pageable pageable);

    Page<ApplicationAssignment> findByOfficerIdAndStatusAndReviewCategory(
            Long officerId, String status, String reviewCategory, Pageable pageable);

    @Query("SELECT a FROM ApplicationAssignment a WHERE a.officer.id = :officerId AND a.status = 'ACTIVE' ORDER BY " +
            "CASE WHEN a.priority = 'HIGH' THEN 1 WHEN a.priority = 'MEDIUM' THEN 2 ELSE 3 END, a.assignedAt ASC")
    List<ApplicationAssignment> findActiveWorkQueueForOfficer(@Param("officerId") Long officerId);

    @Query("SELECT a FROM ApplicationAssignment a WHERE a.status = 'ACTIVE' AND a.slaDueAt < :now")
    List<ApplicationAssignment> findOverdueAssignments(@Param("now") LocalDateTime now);

    long countByOfficerIdAndStatus(Long officerId, String status);

    long countByOfficerIdAndStatusAndReviewCategory(Long officerId, String status, String reviewCategory);

    long countByOfficerIdAndStatusAndPriority(Long officerId, String status, String priority);
}
