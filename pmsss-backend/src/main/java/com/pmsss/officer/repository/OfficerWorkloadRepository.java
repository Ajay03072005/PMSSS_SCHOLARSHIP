package com.pmsss.officer.repository;

import com.pmsss.officer.entity.OfficerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficerWorkloadRepository extends JpaRepository<OfficerWorkload, Long> {

    Optional<OfficerWorkload> findByOfficerId(Long officerId);

    Optional<OfficerWorkload> findByOfficerEmail(String officerEmail);

    List<OfficerWorkload> findByIsActiveTrueOrderByCurrentWorkloadAsc();

    @Query("SELECT ow FROM OfficerWorkload ow WHERE ow.isActive = true AND ow.currentWorkload < ow.maxCapacity ORDER BY (ow.currentWorkload * 1.0 / ow.maxCapacity) ASC")
    List<OfficerWorkload> findAvailableOfficersByUtilization();

    @Query("SELECT ow FROM OfficerWorkload ow WHERE ow.isActive = true AND ow.currentWorkload >= ow.maxCapacity")
    List<OfficerWorkload> findOverloadedOfficers();
}
