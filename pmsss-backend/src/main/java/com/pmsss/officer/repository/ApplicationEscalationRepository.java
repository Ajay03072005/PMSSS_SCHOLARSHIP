package com.pmsss.officer.repository;

import com.pmsss.officer.entity.ApplicationEscalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationEscalationRepository extends JpaRepository<ApplicationEscalation, Long> {

    List<ApplicationEscalation> findByStatus(String status);

    List<ApplicationEscalation> findByOfficerId(Long officerId);

    List<ApplicationEscalation> findByApplicationId(String applicationId);
}
