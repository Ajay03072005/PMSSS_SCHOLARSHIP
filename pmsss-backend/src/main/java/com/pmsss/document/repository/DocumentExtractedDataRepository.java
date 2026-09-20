package com.pmsss.document.repository;

import com.pmsss.document.entity.DocumentExtractedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentExtractedDataRepository extends JpaRepository<DocumentExtractedData, Long> {
    Optional<DocumentExtractedData> findByDocumentId(Long documentId);
    List<DocumentExtractedData> findByApplicationId(Long applicationId);
    List<DocumentExtractedData> findByCertificateNumber(String certificateNumber);
    List<DocumentExtractedData> findByBankAccountNumber(String bankAccountNumber);
}
