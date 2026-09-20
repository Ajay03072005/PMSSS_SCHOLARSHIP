package com.pmsss.verification.repository;

import com.pmsss.verification.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, Long> {
    List<VerificationRecord> findByApplicationIdOrderByVerifiedAtDesc(Long applicationId);
    List<VerificationRecord> findByDocumentId(Long documentId);
}
