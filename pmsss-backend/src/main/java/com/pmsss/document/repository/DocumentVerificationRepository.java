package com.pmsss.document.repository;

import com.pmsss.document.entity.DocumentVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVerificationRepository extends JpaRepository<DocumentVerification, Long> {

    Optional<DocumentVerification> findByUniqueId(String uniqueId);

    List<DocumentVerification> findByDocumentIdOrderByVerifiedAtDesc(Long documentId);

    Optional<DocumentVerification> findTopByDocumentIdOrderByVerifiedAtDesc(Long documentId);

    List<DocumentVerification> findByVerificationStatus(String verificationStatus);

    void deleteByDocumentId(Long documentId);
}
