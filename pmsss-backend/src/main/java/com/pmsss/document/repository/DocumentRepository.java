package com.pmsss.document.repository;

import com.pmsss.common.enums.DocumentStatus;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByUniqueId(String uniqueId);
    List<Document> findByApplicationId(Long applicationId);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Document d WHERE d.application.applicationId = :appUniqueId")
    List<Document> findByApplicationUniqueId(@org.springframework.data.repository.query.Param("appUniqueId") String appUniqueId);

    Optional<Document> findByApplicationIdAndDocumentType(Long applicationId, DocumentType documentType);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Document d WHERE d.application.applicationId = :appUniqueId AND d.documentType = :docType")
    Optional<Document> findByApplicationUniqueIdAndDocumentType(
            @org.springframework.data.repository.query.Param("appUniqueId") String appUniqueId,
            @org.springframework.data.repository.query.Param("docType") DocumentType docType);

    List<Document> findByVerificationStatus(DocumentStatus status);
    long countByVerificationStatus(DocumentStatus status);
}
