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

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Document d WHERE d.application.id = :applicationId AND d.documentType = :documentType ORDER BY d.version DESC, d.id DESC")
    List<Document> findAllByApplicationIdAndDocumentType(
            @org.springframework.data.repository.query.Param("applicationId") Long applicationId,
            @org.springframework.data.repository.query.Param("documentType") DocumentType documentType);

    default Optional<Document> findByApplicationIdAndDocumentType(Long applicationId, DocumentType documentType) {
        List<Document> list = findAllByApplicationIdAndDocumentType(applicationId, documentType);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Document d WHERE d.application.applicationId = :appUniqueId AND d.documentType = :docType ORDER BY d.version DESC, d.id DESC")
    List<Document> findAllByApplicationUniqueIdAndDocumentType(
            @org.springframework.data.repository.query.Param("appUniqueId") String appUniqueId,
            @org.springframework.data.repository.query.Param("docType") DocumentType docType);

    default Optional<Document> findByApplicationUniqueIdAndDocumentType(String appUniqueId, DocumentType docType) {
        List<Document> list = findAllByApplicationUniqueIdAndDocumentType(appUniqueId, docType);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    List<Document> findByVerificationStatus(DocumentStatus status);
    long countByVerificationStatus(DocumentStatus status);
}
