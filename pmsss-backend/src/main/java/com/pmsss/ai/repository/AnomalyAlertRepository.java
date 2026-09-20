package com.pmsss.ai.repository;

import com.pmsss.ai.entity.AnomalyAlertEntity;
import com.pmsss.common.enums.AnomalySeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyAlertRepository extends JpaRepository<AnomalyAlertEntity, Long> {
    List<AnomalyAlertEntity> findByIsResolvedFalseOrderByDetectedAtDesc();
    List<AnomalyAlertEntity> findByApplicationId(String applicationId);
    long countByIsResolvedFalse();
    long countBySeverityAndIsResolvedFalse(AnomalySeverity severity);
}
