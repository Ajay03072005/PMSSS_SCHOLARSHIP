package com.pmsss.verification.repository;

import com.pmsss.verification.entity.CorrectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionRequestRepository extends JpaRepository<CorrectionRequest, Long> {

    List<CorrectionRequest> findByApplicationId(String applicationId);

    List<CorrectionRequest> findByApplicationIdAndIsResolvedFalse(String applicationId);
}
