package com.pmsss.document.repository;

import com.pmsss.document.entity.DocumentExtraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentExtractionRepository extends JpaRepository<DocumentExtraction, Long> {

    Optional<DocumentExtraction> findByUniqueId(String uniqueId);

    Optional<DocumentExtraction> findByDocumentId(Long documentId);

    List<DocumentExtraction> findByDocumentApplicationId(Long applicationId);

    void deleteByDocumentId(Long documentId);
}
