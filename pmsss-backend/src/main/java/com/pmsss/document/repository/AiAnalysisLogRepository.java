package com.pmsss.document.repository;

import com.pmsss.document.entity.AiAnalysisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiAnalysisLogRepository extends JpaRepository<AiAnalysisLog, Long> {

    Optional<AiAnalysisLog> findByUniqueId(String uniqueId);

    List<AiAnalysisLog> findByDocumentIdOrderByProcessedAtDesc(Long documentId);

    List<AiAnalysisLog> findByApplicationIdOrderByProcessedAtDesc(Long applicationId);

    void deleteByDocumentId(Long documentId);
}
